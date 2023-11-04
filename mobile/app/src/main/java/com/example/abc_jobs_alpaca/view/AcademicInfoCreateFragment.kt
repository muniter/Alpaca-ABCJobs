package com.example.abc_jobs_alpaca.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.AcademicInfoCreateViewModel
import com.example.abc_jobs_alpaca.viewmodel.AcademicInfoViewModel
import com.example.abc_jobs_alpaca.viewmodel.CandidateRegisterViewModel

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
    private var isValidNameInstitution: Boolean = false
    private var isValidTypeDegree: Boolean = false
    private var isValidNameDegree: Boolean = false
    private var isValidDateInit: Boolean = false

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_academic_info_create, container, false)
        val btnSave : Button = view.findViewById(R.id.academicInfoSaveButton)
        val btnCancel : Button = view.findViewById(R.id.academicInfoCancelButton)

        btnSave.setOnClickListener(this)
        btnCancel.setOnClickListener(this)

        //val viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
        //    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        //        @Suppress("UNCHECKED_CAST")
        //        return AcademicInfoViewModel(
        //            ABCJobsRepository(activity!!.application)
        //        ) as T
        //    }
        //})[AcademicInfoCreateViewModel::class.java]
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
                val institutionName =
                    view?.findViewById<EditText>(R.id.editTextInstitution)?.text.toString()
                val degreeName = view?.findViewById<EditText>(R.id.editTextDegree)?.text.toString()
                val degreeTypeSelected =
                    view?.findViewById<AppCompatSpinner>(R.id.spinnerEducationLevel)?.selectedItem.toString()
            }
        }
    }
}