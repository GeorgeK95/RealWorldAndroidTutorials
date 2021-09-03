

package com.example.android.petsave.common.data.api.interceptors

import com.example.android.logging.Logger
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject

class LoggingInterceptor @Inject constructor() : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        Logger.i(message)
    }
}