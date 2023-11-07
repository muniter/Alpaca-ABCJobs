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
import com.example.abc_jobs_alpaca.adapter.TechnicalInfoItemRecyclerViewAdapter
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.view.utils.ConfirmDialogFragment
import com.example.abc_jobs_alpaca.viewmodel.TechnicalInfoViewModel
import kotlinx.coroutines.launch

class TechnicalInfoFragment : Fragment(),
    ConfirmDialogFragment.ConfirmDialogListener{

    private var columnCount = 1
    private val tokenLiveData = MutableLiveData<String?>()
    private lateinit var viewModel: TechnicalInfoViewModel
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
        val view = inflater.inflate(R.layout.fragment_item_technical_list, container, false)
        repository = ABCJobsRepository(requireActivity().application)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TechnicalInfoViewModel(
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[TechnicalInfoViewModel::class.java]

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        tokenLiveData.value = token

        tokenLiveData.observe(viewLifecycleOwner) { token ->
            viewModel.onTokenUpdated(token)
            viewModel.loadTechnicalItemsInfo()
        }

        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                viewModel.technicalInfoList.observe(viewLifecycleOwner) {
                        technicalInfoList ->
                    adapter = technicalInfoList?.let {
                        TechnicalInfoItemRecyclerViewAdapter(it){
                            clickedItem ->
                            val confirmDialogFragment = ConfirmDialogFragment(clickedItem.id, this@TechnicalInfoFragment)
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
            viewModel.loadTechnicalItemsInfo()
        }
    }
    override fun onConfirmDelete(id: Int){
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        if (token != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                val result = repository.deleteTechnicalInfo(token, id)
                if (result.isSuccess) {
                    viewModel.loadTechnicalItemsInfo()
                }
                else {
                    //TODO: Something
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
            TechnicalInfoFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
        }
}