package com.example.abc_jobs_alpaca.adapter

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.abc_jobs_alpaca.R

import com.example.abc_jobs_alpaca.databinding.FragmentShortlistedCandidateItemBinding
import com.example.abc_jobs_alpaca.model.models.ShortlistedCandidateItem
import android.content.Context

class ShortlistedCandidateItemRecyclerViewAdapter(
    private val values: List<ShortlistedCandidateItem>
) : RecyclerView.Adapter<ShortlistedCandidateItemRecyclerViewAdapter.ViewHolder>() {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentShortlistedCandidateItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        context = holder.resultView.context
        holder.resultView.text = if(item.result > 0) context.getString(R.string.shortlisted_label_evaluated) else context.getString(R.string.shortlisted_label_pending)
        when(item.result) {
            in 1 .. 100 -> holder.resultView.setTextColor(Color.GREEN)
            else -> holder.resultView.setTextColor(Color.RED)
        }
        holder.nameView.text = item.fullName
        holder.locationView.text = "${item.city}, ${item.country}"
        holder.resultValueView.text = "${item.result.toString()} %"
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentShortlistedCandidateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val resultView: TextView = binding.shortlistedResult
        val nameView: TextView = binding.shortlistedName
        val locationView: TextView = binding.shortlistedLocation
        val resultValueView: TextView = binding.shortlistedResultValue
    }

}