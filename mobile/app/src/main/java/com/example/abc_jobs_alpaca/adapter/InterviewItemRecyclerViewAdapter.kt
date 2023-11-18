package com.example.abc_jobs_alpaca.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.example.abc_jobs_alpaca.databinding.FragmentItemInterviewBinding
import com.example.abc_jobs_alpaca.model.models.InterviewItem


class InterviewItemRecyclerViewAdapter(
    private val values: List<InterviewItem>
) : RecyclerView.Adapter<InterviewItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentItemInterviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id_vacancy.toString()
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemInterviewBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}