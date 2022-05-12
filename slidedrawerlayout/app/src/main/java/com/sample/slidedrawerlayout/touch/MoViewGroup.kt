package com.sample.slidedrawerlayout.touch

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import timber.log.Timber

class MoViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAtt: Int = 0
) : FrameLayout(context, attrs, defStyleAtt) {

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Timber.d("${switchEvent(ev.actionMasked)} -> 22222.dispatchTouchEvent->")
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Timber.d("${switchEvent(ev.actionMasked)} -> 22222.onInterceptTouchEvent->")
        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        Timber.d("${switchEvent(ev.actionMasked)} -> 22222.onTouchEvent->")
        return super.onTouchEvent(ev)
    }
}

