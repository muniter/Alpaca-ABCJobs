package com.example.abc_jobs_alpaca.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.adapter.VacancyItemRecyclerViewAdapter
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.VacancyViewModel
import kotlinx.coroutines.launch

class VacancyFragment : Fragment() {

    private var columnCount = 1
    private val tokenLiveData = MutableLiveData<String?>()
    private lateinit var viewModel: VacancyViewModel

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
        val view = inflater.inflate(R.layout.fragment_vacancy_list, container, false)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return VacancyViewModel(
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[VacancyViewModel::class.java]

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        tokenLiveData.value = token

        tokenLiveData.observe(viewLifecycleOwner) { tokenReceived ->
            viewModel.onTokenUpdated(tokenReceived)
            lifecycleScope.launch { viewModel.loadVacancyItems() }
        }

        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                viewModel.vacancyList.observe(viewLifecycleOwner) { vacancyList ->
                    if(vacancyList?.size == 0) {
                        showToast(
                            getString(R.string.toast_message_data_not_found)
                        )
                    }
                    adapter = vacancyList?.let {
                        VacancyItemRecyclerViewAdapter(it) { clickedItem ->
                            val bundle = bundleOf("vacancyId" to clickedItem.id)
                            findNavController().navigate(R.id.action_nav_vacancy_list_to_shortlistedCandidatesFragment, bundle)
                        }
                    }
                    view.adapter = adapter
                }
            }
        }
        return view
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            lifecycleScope.launch { viewModel.loadVacancyItems() }
        }
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast.show()
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            VacancyFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}