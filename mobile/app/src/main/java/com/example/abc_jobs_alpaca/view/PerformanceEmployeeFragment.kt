package com.example.abc_jobs_alpaca.view

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.abc_jobs_alpaca.databinding.FragmentPerformanceEmployeeBinding
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.PerformanceEmployeeViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class PerformanceEmployeeFragment : Fragment(),
    View.OnClickListener
    {
    companion object {
        fun newInstance() = PerformanceEmployeeFragment()
    }

    private lateinit var viewModel: PerformanceEmployeeViewModel
    private lateinit var binding: FragmentPerformanceEmployeeBinding
    private val tokenLiveData = MutableLiveData<String?>()
    private var token = ""
    private var employeeId = 0
    private var fullName = ""
    private var result = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            employeeId = it.getInt("employeeId")
            fullName = it.getString("fullName").toString()
            result = it.getInt("result")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", null) ?: ""
        binding = DataBindingUtil.inflate(inflater, com.example.abc_jobs_alpaca.R.layout.fragment_performance_employee, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PerformanceEmployeeViewModel(
                    ABCJobsRepository(activity!!.application),
                    employeeId,
                    fullName,
                    result
                ) as T
            }
        })[PerformanceEmployeeViewModel::class.java]

        lifecycleScope.launch { viewModel.loadEmployeeData() }
        binding.viewModel = viewModel
        tokenLiveData.value = token
        val view = binding.root

        binding.performanceEmployeeName.text = fullName

        val saveBtn = binding.permorfanceEmployeeSaveButton
        saveBtn.setOnClickListener(this)

        val cancelBtn = binding.permorfanceEmployeeCancelButton
        cancelBtn.setOnClickListener(this)


        viewModel.employeeItem.observe(viewLifecycleOwner, Observer {
            Log.i("observer","employeeItem: $it")
        })

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PerformanceEmployeeViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            com.example.abc_jobs_alpaca.R.id.permorfance_employee_save_button -> {
                val resultValue : TextInputEditText = binding.performanceEmployeeResultField
                result = resultValue.text.toString().toInt()
                Log.i("onClick","permorfance_employee_save_button $result $employeeId")
                //lifecycleScope.launch { viewModel.savePerformanceEmployee(token, employeeId, result) }
            }

        }
    }

}