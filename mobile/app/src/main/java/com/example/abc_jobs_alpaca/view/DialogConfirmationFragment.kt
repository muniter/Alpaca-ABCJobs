package com.example.abc_jobs_alpaca.view

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ConfirmDialogFragment(id: Int) : DialogFragment(){

    private val id = id

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmación")
            .setMessage("¿Está seguro que desea eliminar este elemento?")
            .setPositiveButton("Aceptar") { dialog, _ ->
                (parentFragment as AcademicInfoFragment).deleteAcademicItem(id)
                //TODO: Refresh list
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                //TODO: close dialog
            }
            .create()

    companion object {
        const val TAG = "ConfirmDialogFragment"
    }

}
