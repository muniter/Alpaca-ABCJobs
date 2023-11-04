package com.example.abc_jobs_alpaca.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.viewmodel.TechnicalInfoCreateViewModel

class TechnicalInfoCreateFragment : Fragment() {

    companion object {
        fun newInstance() = TechnicalInfoCreateFragment()
    }

    private lateinit var viewModel: TechnicalInfoCreateViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_technical_info_create, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TechnicalInfoCreateViewModel::class.java)
        // TODO: Use the ViewModel
    }

}