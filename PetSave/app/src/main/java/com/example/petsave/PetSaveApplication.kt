package com.example.petsave

import android.app.Application
import com.example.logging.Logger
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