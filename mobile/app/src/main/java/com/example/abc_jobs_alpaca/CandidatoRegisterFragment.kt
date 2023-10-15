package com.example.abc_jobs_alpaca

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CandidatoRegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CandidatoRegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_candidato_register, container, false)

        val editTextName = view.findViewById<TextInputEditText>(R.id.editTextName)
        val labelNameError = view.findViewById<TextView>(R.id.labelNameError)
        editTextName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                val newText = s.toString()
                validateAndShowError(newText, labelNameError)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Este método se llama cuando el texto cambia.
                val newText = s.toString()
                validateAndShowError(newText, labelNameError)
            }

            override fun afterTextChanged(s: Editable?) {
                val newText = s.toString()
                validateAndShowError(newText, labelNameError)
            }
        })
        return view
    }

    private fun validateAndShowError(text: String, labelError: TextView) {
        if (text.isEmpty()) {
            labelError.visibility = View.VISIBLE // Mostrar el mensaje de error
            labelError.text = "El campo de nombre no puede estar vacío"
        } else {
            labelError.visibility = View.GONE // Ocultar el mensaje de error
            labelError.text = ""
        }
    }

}