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
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.adapter.EmployeeRecyclerViewAdapter
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.EmployeeListViewModel
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController

class EmployeeListFragment : Fragment() {
    private var columnCount = 1
    private val tokenLiveData = MutableLiveData<String?>()
    private lateinit var viewModel: EmployeeListViewModel
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
        val view = inflater.inflate(R.layout.fragment_item_employee_list, container, false)
        repository = ABCJobsRepository(requireActivity().application)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return EmployeeListViewModel(
                    repository
                ) as T
            }
        })[EmployeeListViewModel::class.java]

        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        tokenLiveData.value = token

        tokenLiveData.observe(viewLifecycleOwner) { token ->
                viewModel.onTokenUpdated(token)
                lifecycleScope.launch {
                    viewModel.loadEmployeeItems()
                }
        }

        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                viewModel.employeeList?.observe(viewLifecycleOwner) { employeeList ->
                    adapter = employeeList?.let {
                        EmployeeRecyclerViewAdapter(it) { clickeItem ->
                            val bundle = bundleOf(
                                "employeeId" to clickeItem.id,
                                "fullName" to clickeItem.name
                            )
                            findNavController().navigate(
                                R.id.action_nav_employee_list_to_performanceEmployeeFragment,
                                bundle
                            )
                        }
                    }
                    view.adapter = adapter
                }
            }
        }
        return view
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            lifecycleScope.launch { viewModel.loadEmployeeItems() }
        }
    }
    companion object {
        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            EmployeeListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}