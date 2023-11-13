package com.example.abc_jobs_alpaca.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.example.abc_jobs_alpaca.databinding.FragmentExamBinding
import com.example.abc_jobs_alpaca.model.models.ExamItemExtend

class ExamRecyclerViewAdapter(
    private val values: List<ExamItemExtend>?,
    private val onItemClick : (ExamItemExtend) -> Unit
) : RecyclerView.Adapter<ExamRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentExamBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values?.get(position)
        if (item != null) {
            holder.idView.text = item.id.toString()
            holder.skillTitle.text = item.exam.skill?.name
            holder.completed.isChecked = item.completed
            if(item.completed){
                holder.btnStartExam.visibility = android.view.View.GONE
            }
            else{
                holder.btnStartExam.visibility = android.view.View.VISIBLE
            }

            if(item.result == null)
                holder.numberOfQuestions.text = "0"
            else
                holder.numberOfQuestions.text = (item.result * 100 / item.exam.number_of_questions).toString()
        }

        holder.btnStartExam.setOnClickListener {
            onItemClick(item!!)
        }
    }

    override fun getItemCount(): Int = values?.size!!

    inner class ViewHolder(binding: FragmentExamBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val skillTitle : TextView = binding.skillTextView
        val completed: CheckBox = binding.stateExam
        val numberOfQuestions: TextView = binding.scoreTextView
        val btnStartExam = binding.startExamBtn
    }

}