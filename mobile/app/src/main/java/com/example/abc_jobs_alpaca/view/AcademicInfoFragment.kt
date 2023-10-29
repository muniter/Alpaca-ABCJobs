package com.example.abc_jobs_alpaca.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentAcademicInfoBinding
import com.example.abc_jobs_alpaca.viewmodel.AcademicInfoViewModel

class AcademicInfoFragment : Fragment() {

    companion object {
        fun newInstance() = AcademicInfoFragment()
    }
    private lateinit var viewModel: AcademicInfoViewModel
    private var _binding: FragmentAcademicInfoBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val academicInfoViewModel =
            ViewModelProvider(this).get(AcademicInfoViewModel::class.java)

        _binding = FragmentAcademicInfoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textAcademicInfo
        academicInfoViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return root


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AcademicInfoViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}