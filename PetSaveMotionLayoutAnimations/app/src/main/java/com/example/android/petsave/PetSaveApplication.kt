package com.example.android.petsave

import android.app.Application
import com.example.android.logging.Logger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PetSaveApplication : Application() {

    // initiate analytics, crashlytics, etc

    override fun onCreate() {
        super.onCreate()

        initLogger()
    }

    private fun initLogger() {
        Logger.init()
    }
}