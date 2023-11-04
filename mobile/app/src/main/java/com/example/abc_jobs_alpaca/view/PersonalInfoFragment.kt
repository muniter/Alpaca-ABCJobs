package com.example.abc_jobs_alpaca.view

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentPersonalInfoBinding
import com.example.abc_jobs_alpaca.model.models.Country
import com.example.abc_jobs_alpaca.model.models.PersonalInfoRequest
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.PersonalInfoViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PersonalInfoFragment : Fragment(), View.OnClickListener, AdapterView.OnItemSelectedListener {

    companion object {
        fun newInstance() = PersonalInfoFragment()
    }

    private lateinit var viewModel: PersonalInfoViewModel
    private lateinit var binding: FragmentPersonalInfoBinding

    private val tokenLiveData = MutableLiveData<String?>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        var token = sharedPreferences.getString("token", null) ?: ""
        var dateFormat = sharedPreferences.getString("dateFormat", "dd/MM/yyyy") ?: ""

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_personal_info, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PersonalInfoViewModel(token, dateFormat, ABCJobsRepository(activity!!.application)) as T
            }
        })[PersonalInfoViewModel::class.java]

        binding.viewModel = viewModel

        tokenLiveData.value = token

        val view = binding.root

        val saveBtn: Button = view.findViewById(R.id.button_save_personal_info)
        saveBtn.setOnClickListener(this)
        val cancelBtn: Button = view.findViewById(R.id.button_cancel_personal_info)
        cancelBtn.setOnClickListener(this)
        val addBtn: FloatingActionButton = view.findViewById(R.id.addOrEditButton)
        addBtn.setOnClickListener(this)
        val editTextDate: EditText = view.findViewById(R.id.editTextDate)
        editTextDate.setOnClickListener(this)

        val spinner: Spinner = view.findViewById(R.id.countrySpinner)
        spinner.onItemSelectedListener=this

        viewModel.personalInfo.observe(viewLifecycleOwner, Observer {
            if(it?.country_code != null){
                var index = viewModel.countrys.value?.indexOfFirst { country -> country?.num_code == it.country_code }

                if(index != null && index >= 0){
                    spinner.setSelection(index)
                }
            }
        })

        (activity as MainActivity).hideButton();

        return view
    }



    private fun showDatePicker() {

        var year = viewModel.personalInfo.value?.birth_date?.year ?: 0
        var month = viewModel.personalInfo.value?.birth_date?.month ?: 0
        var day = viewModel.personalInfo.value?.birth_date?.date ?: 1

        var cal = Calendar.getInstance()
        val dpd = DatePickerDialog(this.requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            viewModel.personalInfo.value?.birth_date = Date(year-1900, monthOfYear, dayOfMonth)

            viewModel.updateDateString()
        }, year+1900, month, day)

        dpd.show()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_save_personal_info -> {
                val birthDate = viewModel.personalInfo.value?.birth_date
                var birthDateString: String? = null

                // Format the selected date into a string

                if (birthDate != null) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formattedDate = dateFormat.format(birthDate)

                    birthDateString = formattedDate
                }

                val countryCode =
                    (view?.findViewById<Spinner>(R.id.countrySpinner)?.selectedItem as Country?)?.num_code
                val city =
                    view?.findViewById<TextInputEditText>(R.id.editTextCity)?.text?.toString()
                val address =
                    view?.findViewById<TextInputEditText>(R.id.editTextAddress)?.text?.toString()
                val phone =
                    view?.findViewById<TextInputEditText>(R.id.editTextPhone)?.text?.toString()
                val biography =
                    view?.findViewById<TextInputEditText>(R.id.editTextBio)?.text?.toString()
                val languages = null
                //toggleControl(false)

                lifecycleScope.launch(Dispatchers.Main) {
                    var personalInfoRequest = PersonalInfoRequest(
                        if (birthDateString == "") null else birthDateString,
                        countryCode,
                        if (city == "") null else city,
                        if (address == "") null else address,
                        if (phone == "") null else phone,
                        if (biography == "") null else biography,
                        languages
                    )
                    viewModel.savePersonalInfo(personalInfoRequest)
                }

                viewModel.enableForm.postValue(false)
            }

            R.id.editTextDate -> {
                showDatePicker()
            }

            R.id.button_cancel_personal_info -> {
                viewModel.enableForm.postValue(false)
            }

            R.id.addOrEditButton -> {
                viewModel.enableForm.postValue(true)
                viewModel.showForm.postValue(true)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        parent!!.setSelection(position)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        var a = 1
    }

}