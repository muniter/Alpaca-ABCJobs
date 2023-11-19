package com.example.abc_jobs_alpaca.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentTechnicalProofBinding
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.TechnicalProofViewModel
import kotlinx.coroutines.launch

class TechnicalProofFragment : Fragment() {

    companion object {
        fun newInstance() = TechnicalProofFragment()
    }

    private var fullName = ""
    private var country = ""
    private var city = ""
    private var result = 0
    private lateinit var viewModel: TechnicalProofViewModel
    private lateinit var binding: FragmentTechnicalProofBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            fullName = it.getString("fullName").toString()
            country = it.getString("country").toString()
            city = it.getString("city").toString()
            result = it.getInt("result")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_technical_proof, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TechnicalProofViewModel(
                    ABCJobsRepository(activity!!.application),
                    fullName,
                    country,
                    city,
                    result
                ) as T
            }
        })[TechnicalProofViewModel::class.java]

        lifecycleScope.launch { viewModel.loadTechnicalProofData() }

        binding.viewModel = viewModel

        val view = binding.root

        viewModel.shortlistedCandidateItem.observe(viewLifecycleOwner, Observer {
            Log.i("observer","shortlistedCandidateItem")
        })

        return view
    }


}