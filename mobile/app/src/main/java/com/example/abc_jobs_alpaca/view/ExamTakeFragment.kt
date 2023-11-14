package com.example.abc_jobs_alpaca.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentExamTakeBinding
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.ExamTakeViewModel
import com.example.abc_jobs_alpaca.viewmodel.TechnicalInfoCreateViewModel
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class ExamTakeFragment : Fragment(),
    View.OnClickListener{

    private lateinit var binding: FragmentExamTakeBinding
    private lateinit var viewModel: ExamTakeViewModel

    private val tokenLiveData = MutableLiveData<String?>()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        requireActivity().title = getString(R.string.take_exam_title)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_exam_take,
            container,
            false
        )
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ExamTakeViewModel(
                    ABCJobsRepository(activity!!.application)
                ) as T
            }
        })[ExamTakeViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        val view =  inflater.inflate(R.layout.fragment_exam_take, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        val btnSubmit = view?.findViewById<Button>(R.id.submitButton)
        btnSubmit?.setOnClickListener(this)

        val btnLeave = view?.findViewById<Button>(R.id.leaveButton)
        btnLeave?.setOnClickListener(this)

        val checkbox = view?.findViewById<RadioButton>(R.id.radioButton)
        val checkbox2 = view?.findViewById<RadioButton>(R.id.radioButton2)
        val checkbox3 = view?.findViewById<RadioButton>(R.id.radioButton3)
        val checkbox4 = view?.findViewById<RadioButton>(R.id.radioButton4)

        checkbox?.setOnCheckedChangeListener {
                _, isChecked ->
            if (isChecked) {
                checkbox2?.isChecked = false
                checkbox3?.isChecked = false
                checkbox4?.isChecked = false
            }
        }

        checkbox2?.setOnCheckedChangeListener {
                _, isChecked ->
            if (isChecked) {
                checkbox?.isChecked = false
                checkbox3?.isChecked = false
                checkbox4?.isChecked = false
            }
        }

        checkbox3?.setOnCheckedChangeListener {
                _, isChecked ->
            if (isChecked) {
                checkbox?.isChecked = false
                checkbox2?.isChecked = false
                checkbox4?.isChecked = false
            }
        }

        checkbox4?.setOnCheckedChangeListener {
                _, isChecked ->
            if (isChecked) {
                checkbox?.isChecked = false
                checkbox2?.isChecked = false
                checkbox3?.isChecked = false
            }
        }

        val args: ExamTakeFragmentArgs by navArgs()
        val examId = args.examId

        tokenLiveData.value = token
        tokenLiveData.observe(viewLifecycleOwner) { token ->
            viewModel.viewModelScope.launch {
                viewModel.onTokenUpdated(token)
                viewModel.postStartExam(examId)
            }

        }
        viewModel.question.observe(viewLifecycleOwner) { question ->
            if(question != null){
                view?.findViewById<TextView>(R.id.questionBodyTextView)?.text = question?.question
                if (checkbox != null) { checkbox.isChecked = false }
                if (checkbox2 != null) { checkbox2.isChecked = false }
                if (checkbox3 != null) { checkbox3.isChecked = false }
                if (checkbox4 != null) {checkbox4.isChecked = false }
            }
            else{
                view?.findNavController()?.navigate(R.id.action_examTakeFragment_to_nav_exam_list)
            }
        }
        viewModel.answers.observe(viewLifecycleOwner) { answers ->
            if(answers != null){
                view?.findViewById<TextView>(R.id.answerTextView)?.text = answers?.get(0)?.answer
                view?.findViewById<TextView>(R.id.answer2TextView)?.text = answers?.get(1)?.answer
                view?.findViewById<TextView>(R.id.answer3TextView)?.text = answers?.get(2)?.answer
                view?.findViewById<TextView>(R.id.answer4TextView)?.text = answers?.get(3)?.answer
                view?.findViewById<TextView>(R.id.idOptionTextView)?.text = answers?.get(0)?.id.toString()
                view?.findViewById<TextView>(R.id.idOptionTextView2)?.text = answers?.get(1)?.id.toString()
                view?.findViewById<TextView>(R.id.idOptionTextView3)?.text = answers?.get(2)?.id.toString()
                view?.findViewById<TextView>(R.id.idOptionTextView4)?.text = answers?.get(3)?.id.toString()
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ExamTakeViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.submitButton -> {
                val idOption = when {
                    view?.findViewById<RadioButton>(R.id.radioButton)?.isChecked == true ->
                        view?.findViewById<TextView>(R.id.idOptionTextView)?.text.toString().toInt()
                    view?.findViewById<RadioButton>(R.id.radioButton2)?.isChecked == true ->
                        view?.findViewById<TextView>(R.id.idOptionTextView2)?.text.toString().toInt()
                    view?.findViewById<RadioButton>(R.id.radioButton3)?.isChecked == true ->
                        view?.findViewById<TextView>(R.id.idOptionTextView3)?.text.toString().toInt()
                    view?.findViewById<RadioButton>(R.id.radioButton4)?.isChecked == true ->
                        view?.findViewById<TextView>(R.id.idOptionTextView4)?.text.toString().toInt()
                    else -> {
                        Log.d("ExamTakeFragment", "No option selected")
                        return
                    }
                }
                viewModel.viewModelScope.launch {
                    viewModel.submitAnswer(idOption)
                }
            }
            R.id.leaveButton -> {
                view?.findNavController()?.navigate(R.id.action_examTakeFragment_to_nav_exam_list)
            }
        }
    }

}