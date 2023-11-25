package com.example.abc_jobs_alpaca.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentShortlistedCandidateItemBinding
import com.example.abc_jobs_alpaca.model.models.ShortlistedCandidateItem
import com.google.gson.JsonObject
import org.json.JSONObject

class ShortlistedCandidateItemRecyclerViewAdapter(
    private val values: List<ShortlistedCandidateItem>,
    private val onItemClick : (ShortlistedCandidateItem) -> Unit
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
        if(item.city != JSONObject.NULL && item.country != JSONObject.NULL)
            holder.locationView.text = "${item.city}, ${item.country}"
        else if(item.city !=JSONObject.NULL)
            holder.locationView.text = item.city
        else if(item.country != JSONObject.NULL)
            holder.locationView.text = item.country
        else
            holder.locationView.text = ""

        holder.resultValueView.text = "${item.result.toString()} %"

        val buttonView = holder.buttonResultSet
        buttonView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentShortlistedCandidateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val resultView: TextView = binding.shortlistedResult
        val nameView: TextView = binding.shortlistedName
        val locationView: TextView = binding.shortlistedLocation
        val resultValueView: TextView = binding.shortlistedResultValue
        val buttonResultSet: Button = binding.shortlistedResultSet
    }

}