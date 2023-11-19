package com.example.abc_jobs_alpaca.adapter

import android.text.format.Time
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.example.abc_jobs_alpaca.databinding.FragmentItemInterviewBinding
import com.example.abc_jobs_alpaca.model.models.InterviewItem
import java.text.SimpleDateFormat

import com.example.abc_jobs_alpaca.utils.DateUtils.dateFormatted
import com.example.abc_jobs_alpaca.utils.DateUtils.timeFormatted
import java.util.Date

class InterviewItemRecyclerViewAdapter(
    private val values: List<InterviewItem>,
    private val dateFormat: String,
    private val timeFormat: String,
    private val pendingMessage: String,
    private val completedMessage: String,
    private val noResult: String
) : RecyclerView.Adapter<InterviewItemRecyclerViewAdapter.ViewHolder>() {

    private val originalDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

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
        val (dateString, timeString) = dateAndTimeFormat(item)
        holder.dateView.text = "$dateString $timeString"

        if (item.result == "null") {
            holder.resultView.text = item.result.toString()
        } else {
            holder.resultView.text = noResult
        }
    }

    private fun dateAndTimeFormat(item: InterviewItem): Pair<String, String> {
        val dateHour = originalDateFormat.parse(item.interview_date)
        val date = Date(dateHour.time)
        val hours = Date(dateHour.time).hours
        val minutes = Date(dateHour.time).minutes
        val time = Time()
        time.hour = hours
        time.minute = minutes

        val dateString = dateFormatted(date, dateFormat)
        val timeString = timeFormatted(time, timeFormat)
        return Pair(dateString, timeString)
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

}