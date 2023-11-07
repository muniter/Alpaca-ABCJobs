package com.example.abc_jobs_alpaca.view.utils

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.abc_jobs_alpaca.R

class ConfirmDialogFragment(private val id: Int, private val listener: ConfirmDialogListener) : DialogFragment() {

    interface ConfirmDialogListener {
        fun onConfirmDelete(id: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.title_dialog))
            .setMessage(getString(R.string.question_dialog))
            .setPositiveButton(getString(R.string.confirm_message)) { dialog, _ ->
                listener.onConfirmDelete(id)
                dismiss()
            }
            .setNegativeButton(getString(R.string.cancel_message)) { dialog, _ ->
                dismiss()
            }
            .create()

    companion object {
        const val TAG = "ConfirmDialogFragment"
    }

}
