package com.example.abc_jobs_alpaca.view

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentTechnicalInfoCreateBinding
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.TechnicalInfoCreateViewModel


class TechnicalInfoCreateFragment : Fragment(),
    View.OnClickListener,
    TechnicalInfoCreateViewModel.NavigationListener {

    private val tokenLiveData = MutableLiveData<String?>()
    private var isValidTypeTechnicalItem: Boolean = false

    private lateinit var viewModel: TechnicalInfoCreateViewModel
    private lateinit var binding: FragmentTechnicalInfoCreateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        requireActivity().title = "Crear información técnica"

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_technical_info_create,
            container,
            false
        )
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TechnicalInfoCreateViewModel(
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[TechnicalInfoCreateViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        val view = inflater.inflate(
            R.layout.fragment_technical_info_create,
            container,
            false
        )

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        val btnSave: Button = view.findViewById(R.id.technicalInfoSaveButton)
        val btnCancel : Button = view.findViewById(R.id.technicalInfoCancelButton)
        val spinnerTechnicalTypeItem = view.findViewById<AppCompatSpinner>(R.id.spinnerTechnicalItem)
        val labelTypeTechnicalItemError = view.findViewById<TextView>(R.id.labelTechnicalItemError)
        val editTextDescription = view.findViewById<TextView>(R.id.editTextDescriptionInfo)
        val labelDescriptionError = view.findViewById<TextView>(R.id.labelDescriptionInfoError)
        val selectedTypeTechnicalItem = spinnerTechnicalTypeItem.selectedItem?.toString()

        tokenLiveData.value = token
        tokenLiveData.observe(viewLifecycleOwner) { token ->
            viewModel.onTokenUpdated(token)
            viewModel.getTypesTechnicalItems()
        }

        viewModel.typesTechnicalInfoTypes.observe(viewLifecycleOwner) { typesTechnicalInfoTypes ->
            if (typesTechnicalInfoTypes != null) {
                val typesTechnicalInfoTypesNames = typesTechnicalInfoTypes.map { it.name }
                val adapter = context?.let {
                    ArrayAdapter(
                        it,
                        android.R.layout.simple_spinner_item,
                        typesTechnicalInfoTypesNames
                    )
                }
                adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerTechnicalTypeItem.adapter = adapter
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TechnicalInfoCreateViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun navigateToNextScreen() {
        view?.findNavController()?.navigate(R.id.action_technicalInfoCreateFragment_to_nav_technical_info)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.technicalInfoSaveButton -> {
                if (isValidTypeTechnicalItem) {

                }
            }
        }
    }

}