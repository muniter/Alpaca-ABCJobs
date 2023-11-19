package com.example.abc_jobs_alpaca.view

import android.content.Context
import android.os.Bundle
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
import com.example.abc_jobs_alpaca.adapter.InterviewItemRecyclerViewAdapter
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.InterviewViewModel
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class InterviewItemFragment : Fragment() {

    private var columnCount = 1
    private val tokenLiveData = MutableLiveData<String?>()
    private lateinit var viewModel: InterviewViewModel
    private lateinit var repository: ABCJobsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val pendingMessage = getString(R.string.pending_message)
        val completeMessage = getString(R.string.completed_message)
        val noResults = getString(R.string.noresults_messages)
        val view = inflater.inflate(R.layout.fragment_item_interview_list, container, false)
        repository = ABCJobsRepository(requireActivity().application)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return InterviewViewModel(
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[InterviewViewModel::class.java]

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        tokenLiveData.value = token

        tokenLiveData.observe(viewLifecycleOwner) { token ->
            viewModel.onTokenUpdated(token)
            lifecycleScope.launch { viewModel.loadInterviewsItemsInfo() }
        }

        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                viewModel.interviewsInfoList.observe(viewLifecycleOwner) { interviewsInfoList ->
                    val dateFormat = sharedPreferences.getString("dateFormat", "")!!
                    val timeFormat = sharedPreferences.getString("timeFormat", "")!!
                    adapter = interviewsInfoList?.let {
                            InterviewItemRecyclerViewAdapter(
                                it,
                                dateFormat,
                                timeFormat,
                                pendingMessage,
                                completeMessage,
                                noResults
                            )
                        }
                    }
                    view.adapter = adapter
                }
            }
        (activity as MainActivity).unhideButton();
        return view
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            lifecycleScope.launch { viewModel.loadInterviewsItemsInfo() }
        }
    }
}