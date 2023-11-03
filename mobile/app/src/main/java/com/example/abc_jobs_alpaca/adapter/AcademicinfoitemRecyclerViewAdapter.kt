package com.example.abc_jobs_alpaca.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView

import com.example.abc_jobs_alpaca.databinding.FragmentItemBinding
import com.example.abc_jobs_alpaca.model.models.AcademicInfoItem

class AcademicinfoitemRecyclerViewAdapter(
    private val values: List<AcademicInfoItem>,
    private val onItemClick : (AcademicInfoItem) -> Unit
) : RecyclerView.Adapter<AcademicinfoitemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id.toString()
        holder.contentView.text = item.institution

        holder.btnDelete.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content
        val btnDelete: ImageButton = binding.deleteAcademicInfoItem

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}