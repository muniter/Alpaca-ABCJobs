package com.example.abc_jobs_alpaca.view

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentAcademicInfoCreateBinding
import com.example.abc_jobs_alpaca.model.models.AcademicInfoRequest
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.AcademicInfoCreateViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AcademicInfoCreateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AcademicInfoCreateFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val tokenLiveData = MutableLiveData<String?>()
    //TODO: Validations
    private var isValidNameInstitution: Boolean = false
    private var isValidTypeDegree: Boolean = false
    private var isValidNameDegree: Boolean = false
    private var isValidDateInit: Boolean = false
    private lateinit var binding: FragmentAcademicInfoCreateBinding
    private lateinit var viewModel: AcademicInfoCreateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_academic_info_create, container, false)
        val btnSave : Button = view.findViewById(R.id.academicInfoSaveButton)
        val btnCancel : Button = view.findViewById(R.id.academicInfoCancelButton)

        btnSave.setOnClickListener(this)
        btnCancel.setOnClickListener(this)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_academic_info_create,
            container,
            false
        )
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AcademicInfoCreateViewModel(
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[AcademicInfoCreateViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val spinnerEducationLevel = view.findViewById<AppCompatSpinner>(R.id.spinnerEducationLevel)
        val spinnerStartYear = view.findViewById<AppCompatSpinner>(R.id.spinnerStartDate)

        tokenLiveData.value = token

        tokenLiveData.observe(viewLifecycleOwner) { token ->
            viewModel.onTokenUpdated(token)
            viewModel.getTypesDegree()
        }

        viewModel.typesTitles.observe(viewLifecycleOwner) { types ->
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                types.map { it.name }
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerEducationLevel.adapter = adapter
        }

        val yearsAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            viewModel.getYears()
        )
         if(yearsAdapter != null){
             yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
             spinnerStartYear.adapter = yearsAdapter
         }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AcademicInfoCreateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AcademicInfoCreateFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.academicInfoSaveButton -> {
                val institution =
                    view?.findViewById<EditText>(R.id.editTextInstitution)?.text.toString()
                val title = view?.findViewById<EditText>(R.id.editTextDegree)?.text.toString()

                val startYear =
                    view?.findViewById<Spinner>(R.id.spinnerStartDate)?.selectedItem.toString().toInt()

                // TODO: Valid for end date
                // TODO: Valid that end date is greater than start date

                val endStart = 0
                // TODO: Valid if the checkbox is checked

                val achievement =
                    view?.findViewById<EditText>(R.id.editTextAdditionalInfo)?.text.toString()

                val degreeTypeSelected =
                    view?.findViewById<Spinner>(R.id.spinnerEducationLevel)?.selectedItem.toString()
                val typeDegree = viewModel.getIdTypeDegree(degreeTypeSelected)

                viewModel.saveAcademicInfoItem(
                    AcademicInfoRequest(institution, title, startYear, endStart, achievement, typeDegree)
                )

            }
        }
    }
}