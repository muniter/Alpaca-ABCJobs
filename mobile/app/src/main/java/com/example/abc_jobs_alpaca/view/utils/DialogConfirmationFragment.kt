package com.example.abc_jobs_alpaca.view.utils

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ConfirmDialogFragment(private val id: Int, private val listener: ConfirmDialogListener) : DialogFragment() {

    interface ConfirmDialogListener {
        fun onConfirmDelete(id: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmación")
            .setMessage("¿Está seguro que desea eliminar este elemento?")
            .setPositiveButton("Aceptar") { dialog, _ ->
                listener.onConfirmDelete(id)
                dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dismiss()
            }
            .create()

    companion object {
        const val TAG = "ConfirmDialogFragment"
    }

}
