package com.example.abc_jobs_alpaca.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.adapter.ExamRecyclerViewAdapter
import com.example.abc_jobs_alpaca.databinding.FragmentExamListBinding
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.ExamListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A fragment representing a list of Items.
 */
class ExamFragment : Fragment() {

    private var columnCount = 1
    private val tokenLiveData = MutableLiveData<String?>()
    private lateinit var viewModel: ExamListViewModel
    private lateinit var repository: ABCJobsRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exam_list, container, false)
        repository = ABCJobsRepository(requireActivity().application)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ExamListViewModel(
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[ExamListViewModel::class.java]

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        tokenLiveData.value = token

        tokenLiveData.observe(viewLifecycleOwner) { token ->
            viewModel.onTokenUpdated(token)
            viewModel.viewModelScope.launch {
                try {
                    withContext(Dispatchers.IO) {

                        val exams = viewModel.loadExams()
                        val examsResult = viewModel.loadExamsResult()

                        // Change to main thread to manage LiveData
                        withContext(Dispatchers.Main) {
                            if (exams != null && examsResult != null && viewModel.exams.value != null && viewModel.examsResult.value != null) {
                                viewModel.mapExamsResult(viewModel.exams, viewModel.examsResult)
                            }
                        }
                    }
                } catch (e: Exception) {
                    //TODO: Handle error
                }
            }
        }

        viewModel.examsResultMapped.observe(viewLifecycleOwner) { exams ->
            Log.d("ExamFragment", "ExamFragmentMapped: ${exams}")
            if (view is RecyclerView) {
                with(view) {
                    layoutManager = when {
                        columnCount <= 1 -> LinearLayoutManager(context)
                        else -> GridLayoutManager(context, columnCount)
                    }
                    adapter = ExamRecyclerViewAdapter(exams) {
                        val action =
                            ExamFragmentDirections.actionNavExamListToExamTakeFragment().setExamId(it.exam.id)
                        view.findNavController().navigate(action)
                    }
                }
            }
        }
        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ExamFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}