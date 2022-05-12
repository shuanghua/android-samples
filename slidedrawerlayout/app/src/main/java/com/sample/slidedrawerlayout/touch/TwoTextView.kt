package com.sample.slidedrawerlayout.touch

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import timber.log.Timber

class TwoTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAtt: Int = 0
) : AppCompatTextView(context, attrs, defStyleAtt) {
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Timber.d("    ${switchEvent(ev.actionMasked)} -> Two.dispatchTouchEvent->")
        return super.dispatchTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        Timber.d("    ${switchEvent(ev.actionMasked)} -> Two.onTouchEvent->")
        return super.onTouchEvent(ev)
    }
}