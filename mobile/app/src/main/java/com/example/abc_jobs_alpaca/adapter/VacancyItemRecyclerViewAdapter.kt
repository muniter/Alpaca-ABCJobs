package com.example.abc_jobs_alpaca.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.example.abc_jobs_alpaca.databinding.FragmentVacancyItemBinding
import com.example.abc_jobs_alpaca.model.models.VacancyItem

class VacancyItemRecyclerViewAdapter(
    private val values: List<VacancyItem>,
    private val onItemClick : (VacancyItem) -> Unit
) : RecyclerView.Adapter<VacancyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentVacancyItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.nameView.text = item.name
        holder.descriptionView.text = item.description
        holder.teamNameView.text = item.team.name

        val itemView = holder.itemView
        itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentVacancyItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val nameView: TextView = binding.vacancyName
        val descriptionView: TextView = binding.vacancyDescription
        val teamNameView: TextView = binding.vacancyTeamName
    }

}