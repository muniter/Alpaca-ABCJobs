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
            .setPositiveButton("Guardar") { dialog, which ->
                Log.d("ConfirmDialogFragment", "Positive button clicked$id")
                // how to call a method of parentFragment
                (parentFragment as AcademicInfoFragment).deleteAcademicItem(id)
            }
            .setNegativeButton("Cancelar") { dialog, which ->
                Log.d("ConfirmDialogFragment", "Negative button clicked")
            }
            .create()

    companion object {
        const val TAG = "ConfirmDialogFragment"
    }

}
