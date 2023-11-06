package com.example.abc_jobs_alpaca.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
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
        holder.companyView.text = item.company
        holder.roleView.text = item.role
        holder.startYear.text = item.startYear.toString()
        holder.endYear.text = item.endYear.toString()
        val state = item.endYear!! > 0
        holder.checkBox.isChecked = state
        holder.checkBox.isEnabled = false
        holder.description.text = item.description
        holder.endYear.visibility = if (state) TextView.VISIBLE else TextView.GONE
        holder.labelEndYear.visibility = if (state) TextView.VISIBLE else TextView.GONE

        holder.btnDelete.setOnClickListener {
            onItemClick(item)
        }

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentWorkItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val roleView : TextView = binding.editTextRoleE
        val companyView : TextView = binding.editTextCompanyE
        val startYear : TextView = binding.spinnerStartDateCE
        val endYear : TextView = binding.spinnerEndDateCE
        val description : TextView = binding.editTextDescriptionWorkE
        val checkBox: CheckBox = binding.checkBoxCompletedJobE
        val labelEndYear : TextView = binding.labelEndDateCE
        val btnDelete : ImageButton = binding.deleteWorkInfoItem


        override fun toString(): String {
            return super.toString() + " '" + idView.text + "'"
        }
    }

}