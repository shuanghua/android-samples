package com.sample.slidedrawerlayout

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowMetrics
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

private fun hideSystemUI(window: Window, view: View) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    WindowInsetsControllerCompat(window, view).let { controller ->

        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

private fun showSystemUI(window: Window, view: View) {
    WindowCompat.setDecorFitsSystemWindows(window, true)
    WindowInsetsControllerCompat(window, view).show(WindowInsetsCompat.Type.systemBars())
}

fun getDisplayWidth(context: Context): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics: WindowMetrics =
            (context as Activity).windowManager.maximumWindowMetrics
        windowMetrics.bounds.height()
    } else {
        context.resources.displayMetrics.widthPixels
    }
}

fun getDisplayHeight(context: Context): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics: WindowMetrics =
            (context as Activity).windowManager.maximumWindowMetrics
        windowMetrics.bounds.height()
    } else {
        context.resources.displayMetrics.heightPixels
    }
}