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
import com.example.abc_jobs_alpaca.adapter.AcademicInfoItemRecyclerViewAdapter
import com.example.abc_jobs_alpaca.adapter.VacancyItemRecyclerViewAdapter
import com.example.abc_jobs_alpaca.model.models.TeamItem
import com.example.abc_jobs_alpaca.model.models.VacancyItem
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.view.utils.ConfirmDialogFragment
import com.example.abc_jobs_alpaca.viewmodel.AcademicInfoViewModel
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

        tokenLiveData.observe(viewLifecycleOwner) { token ->
            viewModel.onTokenUpdated(token)
            lifecycleScope.launch { viewModel.loadVacancyItems() }
        }

        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                viewModel.vacancyList.observe(viewLifecycleOwner) { vacancyList ->
                    adapter = vacancyList?.let {
                        VacancyItemRecyclerViewAdapter(it)
                        /*VacancyItemRecyclerViewAdapter(it) { clickedItem ->
                            val confirmDialogFragment = ConfirmDialogFragment(clickedItem.id, this@AcademicInfoFragment)
                            confirmDialogFragment.show(childFragmentManager, "ConfirmDialogFragment")
                        }*/
                    }
                    view.adapter = adapter
                }
                /*val listVacancies = listOf<VacancyItem>(
                    VacancyItem("programador", "descripcion", TeamItem("equipo"))
                )
                adapter = VacancyItemRecyclerViewAdapter(listVacancies)*/
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
            VacancyFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}