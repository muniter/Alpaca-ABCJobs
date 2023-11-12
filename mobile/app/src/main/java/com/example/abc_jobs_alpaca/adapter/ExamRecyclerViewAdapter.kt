package com.example.abc_jobs_alpaca.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView

import com.example.abc_jobs_alpaca.view.placeholder.PlaceholderContent.PlaceholderItem
import com.example.abc_jobs_alpaca.databinding.FragmentExamBinding
import com.example.abc_jobs_alpaca.model.models.ExamItem

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class ExamRecyclerViewAdapter(
    private val values: List<ExamItem>?,
    private val onItemClick : (ExamItem) -> Unit
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
        }
        if (item != null) {
            holder.skillTitle.text = item.skill?.name
        }
        if (item != null) {
            holder.completed.isChecked = item.completed
        }
        if (item != null) {
            holder.numberOfQuestions.text = item.number_of_questions.toString()
        }
        holder.btnStartExam.setOnClickListener {
            onItemClick(item!!)
        }
//        holder.contentView.text = item.content
    }

    override fun getItemCount(): Int = values?.size!!

    inner class ViewHolder(binding: FragmentExamBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val skillTitle : TextView = binding.skillTextView
        val completed: CheckBox = binding.stateExam
        val numberOfQuestions: TextView = binding.scoreTextView
        val btnStartExam = binding.startExamBtn

//        val contentView: TextView = binding.content
//        override fun toString(): String {
//            return super.toString() + " '" + contentView.text + "'"
//        }
    }

}