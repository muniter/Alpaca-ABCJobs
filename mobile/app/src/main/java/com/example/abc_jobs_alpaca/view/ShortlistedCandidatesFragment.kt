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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.adapter.ShortlistedCandidateItemRecyclerViewAdapter
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.ShortlistedCandidateViewModel
import kotlinx.coroutines.launch

class ShortlistedCandidatesFragment : Fragment() {

    private var columnCount = 1
    private var vacancyId = 0
    private val tokenLiveData = MutableLiveData<String?>()
    private lateinit var viewModel: ShortlistedCandidateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            vacancyId = it.getInt("vacancyId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shortlisted_candidates_list, container, false)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ShortlistedCandidateViewModel(
                    ABCJobsRepository(activity!!.application),
                    vacancyId
                ) as T
            }
        })[ShortlistedCandidateViewModel::class.java]

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        tokenLiveData.value = token

        tokenLiveData.observe(viewLifecycleOwner) { tokenReceived ->
            viewModel.onTokenUpdated(tokenReceived)
            lifecycleScope.launch { viewModel.loadShortlistedCandidateItems() }
        }

        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                viewModel.shorlistedCandidateList.observe(viewLifecycleOwner) { shortlistedCandidateList ->
                    if(shortlistedCandidateList?.size == 0) {
                        showToast(
                            getString(R.string.toast_message_data_not_found)
                        )
                    }
                    adapter = shortlistedCandidateList?.let {
                        ShortlistedCandidateItemRecyclerViewAdapter(it)
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
            lifecycleScope.launch { viewModel.loadShortlistedCandidateItems() }
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
            ShortlistedCandidatesFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}