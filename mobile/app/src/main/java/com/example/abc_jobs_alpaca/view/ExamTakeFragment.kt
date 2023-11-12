package com.example.abc_jobs_alpaca.view

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.navArgs
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentExamTakeBinding
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.ExamTakeViewModel
import com.example.abc_jobs_alpaca.viewmodel.TechnicalInfoCreateViewModel


class ExamTakeFragment : Fragment() {

    private lateinit var binding: FragmentExamTakeBinding
    private lateinit var viewModel: ExamTakeViewModel

    private val tokenLiveData = MutableLiveData<String?>()

    companion object {
        fun newInstance() = ExamTakeFragment()
    }

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

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

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

            view?.findViewById<TextView>(R.id.answerTitleTextView)?.text = "Answer 1: " + (question?.answers?.get(0))
            view?.findViewById<TextView>(R.id.answer2TitleTextView)?.text = "Answer 2: " + (question?.answers?.get(1))
            view?.findViewById<TextView>(R.id.answer3TitleTextView)?.text = "Answer 3: " + (question?.answers?.get(2))

        }


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ExamTakeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}