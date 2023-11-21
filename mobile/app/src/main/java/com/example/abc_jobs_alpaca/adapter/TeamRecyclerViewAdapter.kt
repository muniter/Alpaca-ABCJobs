package com.example.abc_jobs_alpaca.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.example.abc_jobs_alpaca.databinding.FragmentItemTeamBinding
import com.example.abc_jobs_alpaca.model.models.Team

class TeamRecyclerViewAdapter(
    private val values: List<Team>
) : RecyclerView.Adapter<TeamRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentItemTeamBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.nameView.text = item.name

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemTeamBinding) :
        RecyclerView.ViewHolder(binding.root) {

            val nameView: TextView = binding.teamTextView


    }

}