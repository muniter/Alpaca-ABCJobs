package com.example.abc_jobs_alpaca.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.example.abc_jobs_alpaca.databinding.FragmentItemInterviewBinding
import com.example.abc_jobs_alpaca.model.models.InterviewItem
import com.example.abc_jobs_alpaca.view.PreferencesFragment.Companion.DATE_FORMAT_1
import com.example.abc_jobs_alpaca.view.PreferencesFragment.Companion.DATE_FORMAT_2
import com.example.abc_jobs_alpaca.view.PreferencesFragment.Companion.DATE_FORMAT_3
import com.example.abc_jobs_alpaca.view.PreferencesFragment.Companion.DATE_FORMAT_4
import java.text.SimpleDateFormat


class InterviewItemRecyclerViewAdapter(
    private val values: List<InterviewItem>,
    private val dateFormat: String,
    private val pendingMessage: String,
    private val completedMessage: String,
    private val noResult: String
) : RecyclerView.Adapter<InterviewItemRecyclerViewAdapter.ViewHolder>() {

    private val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private val newFormat1 = SimpleDateFormat("dd/MM/yyyy HH:mm")
    private val newFormat2 = SimpleDateFormat("dd-MM-yyyy HH:mm")
    private val newFormat3 = SimpleDateFormat("MM/dd/yyyy HH:mm")
    private val newFormat4 = SimpleDateFormat("MM-dd-yyyy HH:mm")


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
        holder.nameVacancyView.text = item.name
        holder.companyView.text = item.company
        if (item.completed) {
            holder.stateView.text = completedMessage
            holder.stateView.setTextColor(0xFF00FF00.toInt())
        } else {
            holder.stateView.text = pendingMessage
            holder.stateView.setTextColor(0xFFFF0000.toInt())
        }

        //val date = originalFormat.parse(item.interview_date)
        //val newDate = when (dateFormat) {
        //    DATE_FORMAT_1 -> newFormat1.format(date).toString()
        //    DATE_FORMAT_2 -> newFormat2.format(date).toString()
        //    DATE_FORMAT_3 -> newFormat3.format(date).toString()
        //    DATE_FORMAT_4 -> newFormat4.format(date).toString()
        //    else -> newFormat1.format(date)
        //}

        //holder.dateView.text = newDate

        holder.dateView.text = item.interview_date


        if (item.result != null) {
            holder.resultView.text = item.result.toString()
        } else {
            holder.resultView.text = noResult
        }


    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemInterviewBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val companyView: TextView = binding.companyInterviewText
        val stateView: TextView = binding.stateInterviewText
        val nameVacancyView: TextView = binding.vacancyNameText
        val dateView: TextView = binding.dateInterviewText
        val resultView : TextView = binding.resultInterviewText

    }

    companion object {
        val DATE_FORMAT_1 = "dd/MM/yyyy"
        val DATE_FORMAT_2 = "dd-MM-yyyy"
        val DATE_FORMAT_3 = "MM/dd/yyyy"
        val DATE_FORMAT_4 = "MM-dd-yyyy"
    }

}