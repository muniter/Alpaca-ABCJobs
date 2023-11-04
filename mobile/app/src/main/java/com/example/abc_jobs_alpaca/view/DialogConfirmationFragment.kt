package com.example.abc_jobs_alpaca.view

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

class ConfirmDialogFragment(id: Int) : DialogFragment(){

    private val id = id


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmación")
            .setMessage("¿Está seguro que desea eliminar este elemento?")
            .setPositiveButton("Aceptar") { dialog, _ ->
                (parentFragment as AcademicInfoFragment).deleteItem(id)
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dismiss()
            }
            .create()

    companion object {
        const val TAG = "ConfirmDialogFragment"
    }

}
