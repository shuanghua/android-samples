package com.example.myapplication

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.core.graphics.drawable.toDrawable

class MyDialog(context: Context) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.dialog_scrollable_inputs)

        setupWindow()
        setupButton()
    }

    private fun setupButton() {
        findViewById<Button>(R.id.btnCancel)?.setOnClickListener {
            dismiss()
        }

        findViewById<Button>(R.id.btnConfirm)?.setOnClickListener {
            dismiss()
        }
    }

    private fun setupWindow() {
        window?.let {
            val displayMetrics = context.resources.displayMetrics
            val maxWidth = (displayMetrics.widthPixels * 0.6).toInt()
            val maxHeight = (displayMetrics.heightPixels * 0.9).toInt()


            val params = it.attributes
            params.width = maxWidth
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.gravity = Gravity.CENTER
            it.attributes = params

//            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)


            findViewById<View>(R.id.scrollView)?.let { scrollView ->
                val layoutParams = scrollView.layoutParams
                layoutParams.height = maxHeight - 250 // 预留空间给底部按钮
                scrollView.layoutParams = layoutParams
            }


        }

    }


}