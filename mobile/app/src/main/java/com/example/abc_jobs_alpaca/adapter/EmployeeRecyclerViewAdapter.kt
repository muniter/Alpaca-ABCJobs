package com.example.abc_jobs_alpaca.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentItemEmployeeBinding
import com.example.abc_jobs_alpaca.model.models.Employee
import com.example.abc_jobs_alpaca.model.models.Evaluation
import com.example.abc_jobs_alpaca.utils.DateUtils.dateFormatted
import java.util.Date

class EmployeeRecyclerViewAdapter(
    private val values: List<Employee>,
    private val onItemClick : (Employee) -> Unit
) : RecyclerView.Adapter<EmployeeRecyclerViewAdapter.ViewHolder>() {

    lateinit var context: Context
    private lateinit var evaluations : List<Evaluation>
    private val date = Date()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentItemEmployeeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        context = holder.stateView.context
        holder.employeeNameView.text = item.name
        evaluations = item.evaluations
        if( evaluacionPendiente(item) != "null")
        {
            holder.stateView.text = context.getString(R.string.employee_label_evaluated)
            holder.stateView.setTextColor(context.getColor(R.color.success_message))
            var sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            var dateFormat = sharedPref.getString("dateFormat", "")
            var evaluationDate = item.evaluations.last().date
            var evaluateDateS = evaluationDate.split("-")
            var evaluateDate = Date(evaluateDateS[0].toInt()-1900, evaluateDateS[1].toInt() - 1, evaluateDateS[2].toInt())
            holder.evaluationDateView.text = dateFormatted(evaluateDate, dateFormat.toString())

            holder.resultView.text = item.evaluations.last().result.toString() + "%"
            holder.setResultButton.isEnabled = false
        }
        else
        {
            holder.stateView.text = context.getString(R.string.employee_label_pending)
            holder.stateView.setTextColor(context.getColor(R.color.error_message))
            holder.evaluationDateView.text = "  "
            holder.resultView.text = "  "
        }
        holder.setResultButton.setOnClickListener {
            onItemClick(item)
        }
    }

    private fun currentMonthEvaluation(employee: Employee): String {
        val currentMonthEvaluation = employee.evaluations.find { evaluation ->
            val parts = evaluation.date.split('-')
            val parsedDate = Date(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
            parsedDate.month == date.month && parsedDate.year == date.year+1900
        }
        return currentMonthEvaluation?.result.toString() ?: ""
    }

    private fun evaluacionPendiente(employee: Employee): String {
        return currentMonthEvaluation(employee)
    }


    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemEmployeeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val stateView: TextView = binding.stateEvaluation
        val employeeNameView: TextView = binding.employeeName
        val evaluationDateView : TextView = binding.evaluationEmployeeDate
        val resultView : TextView = binding.employeeResultValue
        val setResultButton: Button = binding.employeeResultSet

    }

}