package com.example.abc_jobs_alpaca.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView

import com.example.abc_jobs_alpaca.databinding.FragmentAcademicItemBinding
import com.example.abc_jobs_alpaca.model.models.AcademicInfoItem

class AcademicInfoItemRecyclerViewAdapter(
    private val values: List<AcademicInfoItem>,
    private val onItemClick : (AcademicInfoItem) -> Unit
) : RecyclerView.Adapter<AcademicInfoItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentAcademicItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id.toString()
        holder.nameInstitution.text = item.institution
        holder.nameDegree.text = item.title
        holder.typeDegree.text = item.type.name
        holder.startDate.text = item.start_year.toString()
        holder.endDate.text = item.end_year.toString()
        val state = item.end_year > 0
        holder.checkBox.isChecked = state
        holder.checkBox.isEnabled = false
        holder.info.text = item.achievement
        holder.endDate.visibility = if (state) TextView.VISIBLE else TextView.GONE
        holder.labelEndDate.visibility = if (state) TextView.VISIBLE else TextView.GONE

        holder.btnDelete.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentAcademicItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val btnDelete: ImageButton = binding.deleteAcademicInfoItem
        val nameDegree: TextView = binding.editTextDegree
        val nameInstitution: TextView = binding.editTextInstitution
        val typeDegree: TextView = binding.spinnerEducationLevel
        val startDate: TextView = binding.spinnerStartDate
        val checkBox: CheckBox = binding.checkBoxCompletedStudies
        val endDate: TextView = binding.spinnerEndDate
        val labelEndDate : TextView = binding.labelEndDate
        val info: TextView = binding.editTextAdditionalInfo

        override fun toString(): String {
            return super.toString() + " '" + nameDegree.text + "'"
        }
    }

}