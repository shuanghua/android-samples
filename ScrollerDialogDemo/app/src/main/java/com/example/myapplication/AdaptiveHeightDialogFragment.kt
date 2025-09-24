package com.example.myapplication

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment

class AdaptiveHeightDialogFragment : DialogFragment() {

    private var keyboardLayoutListener: OnGlobalLayoutListener? = null
    private var originalHeight = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_scrollable_inputs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup button listeners
        view.findViewById<View>(R.id.btnCancel).setOnClickListener {
            dismiss()
        }

        view.findViewById<View>(R.id.btnConfirm).setOnClickListener {
            // Handle confirm
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.let { window ->
            // Set dialog width to match parent
            val params = window.attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = params

            // Set background transparent
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // KEY: Adjust resize mode to handle keyboard
            window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
            )

            // Set maximum height (90% of screen height)
            setupMaxHeight()

            // Setup keyboard visibility listener
            setupKeyboardVisibilityListener()
        }
    }

    private fun setupMaxHeight() {
        val displayMetrics = resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val maxHeight = (screenHeight * 0.9).toInt() // 90% of screen height

        view?.let { dialogView ->
            val scrollView = dialogView.findViewById<View>(R.id.scrollView)
            scrollView?.let {
                val layoutParams = it.layoutParams
                layoutParams.height = maxHeight - 200 // Leave space for title and buttons
                it.layoutParams = layoutParams
            }
        }
    }

    private fun setupKeyboardVisibilityListener() {
        val rootView = dialog?.window?.decorView?.findViewById<View>(android.R.id.content)

        keyboardLayoutListener = OnGlobalLayoutListener {
            rootView?.let {
                val rect = android.graphics.Rect()
                it.getWindowVisibleDisplayFrame(rect)
                val screenHeight = it.rootView.height
                val keypadHeight = screenHeight - rect.bottom

                if (keypadHeight > screenHeight * 0.15) {
                    // Keyboard is shown
                    onKeyboardShow(keypadHeight)
                } else {
                    // Keyboard is hidden
                    onKeyboardHide()
                }
            }
        }

        rootView?.viewTreeObserver?.addOnGlobalLayoutListener(keyboardLayoutListener)
    }

    private fun onKeyboardShow(keyboardHeight: Int) {
        // Optional: Adjust dialog position or size when keyboard shows
        view?.let { dialogView ->
            val scrollView = dialogView.findViewById<View>(R.id.scrollView)
            scrollView?.let {
                // You can add animation here
                it.animate()
                    .translationY(-keyboardHeight / 4f) // Slight upward movement
                    .setDuration(200)
                    .start()
            }
        }
    }

    private fun onKeyboardHide() {
        // Reset position when keyboard hides
        view?.let { dialogView ->
            val scrollView = dialogView.findViewById<View>(R.id.scrollView)
            scrollView?.let {
                it.animate()
                    .translationY(0f)
                    .setDuration(200)
                    .start()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up keyboard listener
        keyboardLayoutListener?.let {
            dialog?.window?.decorView?.findViewById<View>(android.R.id.content)
                ?.viewTreeObserver?.removeOnGlobalLayoutListener(it)
        }
    }
}