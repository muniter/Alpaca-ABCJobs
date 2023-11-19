package com.example.abc_jobs_alpaca.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentTechnicalProofBinding
import com.example.abc_jobs_alpaca.model.models.TechnicalProofRequest
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.TechnicalProofViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TechnicalProofFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance() = TechnicalProofFragment()
    }

    private var vacancyId = 0
    private var candidateId = 0
    private var fullName = ""
    private var country = ""
    private var city = ""
    private var result = 0
    private lateinit var viewModel: TechnicalProofViewModel
    private lateinit var binding: FragmentTechnicalProofBinding
    private val tokenLiveData = MutableLiveData<String?>()
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            vacancyId = it.getInt("vacancyId")
            candidateId = it.getInt("candidateId")
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
        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", null) ?: ""

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

        tokenLiveData.value = token

        val view = binding.root

        val saveBtn: Button = view.findViewById(R.id.technical_proof_save_button)
        saveBtn.setOnClickListener(this)
        val cancelBtn: Button = view.findViewById(R.id.technical_proof_cancel_button)
        cancelBtn.setOnClickListener(this)

        viewModel.shortlistedCandidateItem.observe(viewLifecycleOwner, Observer {
            Log.i("observer","shortlistedCandidateItem")
        })

        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.technical_proof_save_button -> {
                val resultValue =
                    view?.findViewById<TextInputEditText>(R.id.technical_proof_result_field)?.text?.toString()?.toInt()
                lifecycleScope.launch(Dispatchers.Main) {
                    if (resultValue != null) {
                        var request: ArrayList<TechnicalProofRequest>? = ArrayList()
                        request?.add(TechnicalProofRequest(candidateId,resultValue))
                        if (request != null) {
                            viewModel.saveTechnicalProofResult(token, vacancyId, request)
                            showToast(
                                getString(R.string.technical_proof_saved)
                            )
                            backToShortlistedCandidatesList()
                        }
                    }
                }
            }
            R.id.technical_proof_cancel_button -> {
                backToShortlistedCandidatesList()
            }
        }
    }

    private fun backToShortlistedCandidatesList() {
        val bundle = bundleOf("vacancyId" to vacancyId)
        findNavController().navigate(R.id.action_nav_technical_proof_to_shortlistedCandidatesFragment, bundle)
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast.show()
    }

}