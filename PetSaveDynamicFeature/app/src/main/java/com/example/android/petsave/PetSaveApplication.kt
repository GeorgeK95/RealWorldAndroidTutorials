package com.example.android.petsave

import android.app.Application
import android.content.Context
import com.example.android.logging.Logger
import com.google.android.play.core.splitcompat.SplitCompat
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PetSaveApplication : Application() {

    // initiate analytics, crashlytics, etc

    override fun onCreate() {
        super.onCreate()

        initLogger()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        SplitCompat.install(this)
    }

    private fun initLogger() {
        Logger.init()
    }
}