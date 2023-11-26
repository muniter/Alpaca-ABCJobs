package com.example.abc_jobs_alpaca.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.abc_jobs_alpaca.databinding.FragmentTechnicalItemBinding
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoItem

class TechnicalInfoItemRecyclerViewAdapter(
    private val values: List<TechnicalInfoItem>,
    private val onItemClick : (TechnicalInfoItem) -> Unit
) : RecyclerView.Adapter<TechnicalInfoItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentTechnicalItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id.toString()
        holder.typeTechnicalInfoItem.text = item.type.name
        holder.additionalInfo.text = item.description
        holder.ratingBar.rating = item.score?.toFloat()!!

        holder.btnDelete.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentTechnicalItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val typeTechnicalInfoItem : TextView = binding.spinnerTechnicalItem
        val additionalInfo: TextView = binding.editTextAdditionalInfo
        val btnDelete = binding.deleteTechnicalInfoItem
        val ratingBar = binding.ratingBar

        override fun toString(): String {
            return super.toString() + " '" + typeTechnicalInfoItem.text + "'"
        }
    }

}