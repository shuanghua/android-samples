package com.sample.slidedrawerlayout

import android.app.Application
import android.util.Log
import timber.log.Timber
import timber.log.Timber.*
import timber.log.Timber.Forest.plant


class MoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            plant(DebugTree())
        }
    }
}