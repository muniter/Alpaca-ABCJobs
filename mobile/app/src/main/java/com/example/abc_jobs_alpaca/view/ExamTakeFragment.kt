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
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentExamTakeBinding
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.ExamTakeViewModel
import com.example.abc_jobs_alpaca.viewmodel.TechnicalInfoCreateViewModel
import kotlin.properties.Delegates


class ExamTakeFragment : Fragment(),
    View.OnClickListener{

    private lateinit var binding: FragmentExamTakeBinding
    private lateinit var viewModel: ExamTakeViewModel

    private val tokenLiveData = MutableLiveData<String?>()

    companion object {
        fun newInstance() = ExamTakeFragment()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        requireActivity().title = "Crear información técnica"

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

        //view?.findViewById<Button>(R.id.submitButton)?.isEnabled = false

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        val btnSubmit = view?.findViewById<Button>(R.id.submitButton)
        btnSubmit?.setOnClickListener(this)

        val args: ExamTakeFragmentArgs by navArgs()
        val examId = args.examId

        tokenLiveData.value = token
        tokenLiveData.observe(viewLifecycleOwner) { token ->
            viewModel.onTokenUpdated(token)
            viewModel.postStartExam(examId)
        }

        view?.findViewById<TextView>(R.id.questionTitleTextView)?.text = examId.toString()
        viewModel.question.observe(viewLifecycleOwner) { question ->
            Log.d("ExamTakeFragment", "question: $question")
            view?.findViewById<TextView>(R.id.questionBodyTextView)?.text = question?.question
        }

        viewModel.answers.observe(viewLifecycleOwner) { answers ->
            Log.d("ExamTakeFragment", "answers: $answers")
            view?.findViewById<TextView>(R.id.answerTextView)?.text = answers?.get(0)?.answer
            view?.findViewById<TextView>(R.id.answer2TextView)?.text = answers?.get(1)?.answer
            view?.findViewById<TextView>(R.id.answer3TextView)?.text = answers?.get(2)?.answer

            view?.findViewById<TextView>(R.id.idOptionTextView)?.text = answers?.get(0)?.id.toString()
            view?.findViewById<TextView>(R.id.idOptionTextView2)?.text = answers?.get(1)?.id.toString()
            view?.findViewById<TextView>(R.id.idOptionTextView3)?.text = answers?.get(2)?.id.toString()
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
                Log.d("ExamTakeFragment", "Submit button clicked")
                val radioButton1 = view?.findViewById<RadioButton>(R.id.radioButton)?.isChecked
                val radioButton2 = view?.findViewById<RadioButton>(R.id.radioButton2)?.isChecked
                val radioButton3 = view?.findViewById<RadioButton>(R.id.radioButton3)?.isChecked

                var idOption: Int = 0
                if (radioButton1 == true) {
                    idOption =
                        view?.findViewById<TextView>(R.id.idOptionTextView)?.text.toString().toInt()
                    viewModel.submitAnswer(idOption)
                } else if (radioButton2 == true) {
                    idOption = view?.findViewById<TextView>(R.id.idOptionTextView2)?.text.toString()
                        .toInt()
                    viewModel.submitAnswer(idOption)
                } else if (radioButton3 == true) {
                    idOption = view?.findViewById<TextView>(R.id.idOptionTextView3)?.text.toString()
                        .toInt()
                    viewModel.submitAnswer(idOption)
                } else {
                    Log.d("ExamTakeFragment", "No option selected")
                }
                Log.d("ExamTakeFragment", "idOption: $idOption")
            }
        }
    }
}