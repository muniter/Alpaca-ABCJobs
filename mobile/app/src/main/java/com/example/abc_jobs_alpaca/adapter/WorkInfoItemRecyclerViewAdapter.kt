package com.example.abc_jobs_alpaca.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.abc_jobs_alpaca.databinding.FragmentWorkItemBinding
import com.example.abc_jobs_alpaca.model.models.WorkInfoItem

class WorkInfoItemRecyclerViewAdapter(
    private val values: List<WorkInfoItem>,
    private val onItemClick : (WorkInfoItem) -> Unit
) : RecyclerView.Adapter<WorkInfoItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentWorkItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id.toString()

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentWorkItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber

        override fun toString(): String {
            return super.toString() + " '" + idView.text + "'"
        }
    }

}