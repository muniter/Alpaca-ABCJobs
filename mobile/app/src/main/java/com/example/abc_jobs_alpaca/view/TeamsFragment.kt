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
import com.example.abc_jobs_alpaca.adapter.TeamRecyclerViewAdapter
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.view.placeholder.PlaceholderContent
import com.example.abc_jobs_alpaca.viewmodel.AcademicInfoViewModel
import com.example.abc_jobs_alpaca.viewmodel.TeamListViewModel
import kotlinx.coroutines.launch

class TeamsFragment : Fragment() {

    private var columnCount = 1
    private val tokenLiveData = MutableLiveData<String?>()
    private lateinit var viewModel: TeamListViewModel
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
        val view = inflater.inflate(R.layout.fragment_item_team_list, container, false)
        repository = ABCJobsRepository(requireActivity().application)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TeamListViewModel(
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[TeamListViewModel::class.java]

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        tokenLiveData.value = token

        tokenLiveData.observe(viewLifecycleOwner) { token ->
            viewModel.onTokenUpdated(token)
            lifecycleScope.launch {
                viewModel.loadTeams()
            }
        }

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                viewModel.teamList.observe(viewLifecycleOwner) { teamList ->
                    adapter = TeamRecyclerViewAdapter(teamList ?: emptyList())
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
            lifecycleScope.launch { viewModel.loadTeams() }
        }
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
    }
}