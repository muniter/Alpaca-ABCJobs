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
import com.example.abc_jobs_alpaca.adapter.WorkInfoItemRecyclerViewAdapter
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.view.utils.ConfirmDialogFragment
import com.example.abc_jobs_alpaca.viewmodel.WorkInfoViewModel
import kotlinx.coroutines.launch

class WorkInfoFragment : Fragment(),
    ConfirmDialogFragment.ConfirmDialogListener {

    private var columnCount = 1
    private val tokenLiveData = MutableLiveData<String?>()
    private lateinit var viewModel: WorkInfoViewModel
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
        val view = inflater.inflate(R.layout.fragment_item_work_list, container, false)
        repository = ABCJobsRepository(requireActivity().application)
        viewModel =  ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return WorkInfoViewModel(
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[WorkInfoViewModel::class.java]

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        tokenLiveData.value = token

        tokenLiveData.observe(viewLifecycleOwner) { token ->
            viewModel.onTokenUpdated(token)
            lifecycleScope.launch { viewModel.loadWorkItemsInfo() }
        }

        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                viewModel.workInfoList.observe(viewLifecycleOwner) { workInfoList ->
                    adapter = workInfoList?.let {
                        WorkInfoItemRecyclerViewAdapter(it) { clickedItem ->
                            val confirmDialogFragment = ConfirmDialogFragment(clickedItem.id, this@WorkInfoFragment)
                            confirmDialogFragment.show(childFragmentManager, "ConfirmDialogFragment")
                        }
                    }
                    view.adapter = adapter
                }
            }
        }
        (activity as MainActivity).unhideButton();
        return view
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            lifecycleScope.launch { viewModel.loadWorkItemsInfo() }
        }
    }


    override fun onConfirm(id: Int){
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        if (token != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                val result = repository.deleteWorkInfo(token, id)
                if (result.isSuccess) {
                    viewModel.loadWorkItemsInfo()
                }
                else {
                    Log.d("WorkInfoFragment", "deleteWorkItem: ${result.exceptionOrNull()}")
                }
            }
        }
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            WorkInfoFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}