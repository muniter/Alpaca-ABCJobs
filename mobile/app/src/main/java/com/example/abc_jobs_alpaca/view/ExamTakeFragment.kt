package com.example.abc_jobs_alpaca.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.viewmodel.ExamTakeViewModel

class ExamTakeFragment : Fragment() {

    companion object {
        fun newInstance() = ExamTakeFragment()
    }

    private lateinit var viewModel: ExamTakeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_exam_take, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ExamTakeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}