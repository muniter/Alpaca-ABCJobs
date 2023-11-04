package com.example.abc_jobs_alpaca.view

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
import androidx.lifecycle.lifecycleScope
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.adapter.AcademicInfoItemRecyclerViewAdapter
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.AcademicInfoViewModel
import kotlinx.coroutines.launch

class AcademicInfoFragment : Fragment() {

    private var columnCount = 1
    private val tokenLiveData = MutableLiveData<String?>()
    private lateinit var viewModel: AcademicInfoViewModel
    private lateinit var repository: ABCJobsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_academic_list, container, false)
        repository = ABCJobsRepository(requireActivity().application)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AcademicInfoViewModel(
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[AcademicInfoViewModel::class.java]

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        tokenLiveData.value = token

        tokenLiveData.observe(viewLifecycleOwner) { token ->
            viewModel.onTokenUpdated(token)
            viewModel.loadAcademicItemsInfo()
        }

        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                viewModel.academicInfoList.observe(viewLifecycleOwner) { academicInfoList ->
                    adapter = academicInfoList?.let {
                        AcademicInfoItemRecyclerViewAdapter(it) { clickedItem ->
                            ConfirmDialogFragment(clickedItem.id).show(childFragmentManager, "ConfirmDialogFragment")
                        }
                    }
                    view.adapter = adapter
                }
            }
        }
        return view
    }

    fun deleteItem(id: Int) {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        if (token != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                val result = repository.deleteAcademicInfo(token, id)
                if (result.isSuccess) {
                    //TODO: Check if there is refreshing
                    Log.d("AcademicInfoFragment", "deleteAcademicItem: ${result.getOrNull()}")
                    viewModel.loadAcademicItemsInfo()
                }
                else {
                    Log.d("AcademicInfoFragment", "deleteAcademicItem: ${result.exceptionOrNull()}")
                }
            }
        }
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            AcademicInfoFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}