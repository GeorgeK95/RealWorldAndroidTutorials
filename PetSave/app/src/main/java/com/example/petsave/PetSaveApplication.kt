package com.example.petsave

import android.app.Application
import com.example.logging.Logger

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