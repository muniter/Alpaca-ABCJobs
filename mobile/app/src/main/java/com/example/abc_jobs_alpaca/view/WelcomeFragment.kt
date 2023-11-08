package com.example.abc_jobs_alpaca.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.abc_jobs_alpaca.R
import com.example.abc_jobs_alpaca.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment(), View.OnClickListener {
    private val hideHandler = Handler(Looper.myLooper()!!)

    @Suppress("InlinedApi")
    private val hidePart2Runnable = Runnable {

    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
    }
    private var visible: Boolean = false
    private val hideRunnable = Runnable { hide() }

    private val delayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    private var dummyButton: Button? = null
    private var fullscreenContent: View? = null
    private var fullscreenContentControls: View? = null

    private var _binding: FragmentWelcomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    private var languageChangeListener: OnLanguageChangeListener? = null
    private var elementHideListener: OnElementHideListener? = null


    fun interface OnElementHideListener{
        fun hideElement(elementId: Int)
    }

    fun interface OnLanguageChangeListener {
        fun onLanguageSelected(newLanguage: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when (context) {
            is OnElementHideListener -> elementHideListener = context
            is OnLanguageChangeListener -> languageChangeListener = context
            else -> throw IllegalArgumentException(getString(R.string.context_error_message))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btn: Button = view.findViewById(R.id.button_user_registered)
        btn.setOnClickListener(this)

        val btn2: Button = view.findViewById(R.id.button_welcome_unregistered)
        btn2.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Clear the systemUiVisibility flag
        //activity?.window?.decorView?.systemUiVisibility = 0
        show()
    }

    override fun onDestroy() {
        super.onDestroy()
        dummyButton = null
        fullscreenContent = null
        fullscreenContentControls = null
    }

    private fun hide() {

        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    @Suppress("InlinedApi")
    private fun show() {
        // Show the system bar
        //fullscreenContent?.systemUiVisibility =
        //    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
        //            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        visible = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_welcome_unregistered -> {
                if (elementHideListener != null)
                    elementHideListener?.hideElement(R.id.spinner)

                v?.findNavController()?.navigate(R.id.action_welcomeFragment_to_registerTypeFragment)
            }
            R.id.button_user_registered -> {
                if (elementHideListener != null)
                    elementHideListener?.hideElement(R.id.spinner)
                v?.findNavController()?.navigate(R.id.action_welcomeFragment_to_loginFragment)
            }
        }
    }


}