package com.example.android.petsave.core.data.di

import com.babylon.certificatetransparency.certificateTransparencyInterceptor
import com.example.android.petsave.core.data.api.ApiConstants
import com.example.android.petsave.core.data.api.PetFinderApi
import com.example.android.petsave.core.data.api.interceptors.AuthenticationInterceptor
import com.example.android.petsave.core.data.api.interceptors.LoggingInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class ApiModule {

  @Provides
  @Singleton
  fun provideApi(okHttpClient: OkHttpClient): PetFinderApi {
    return Retrofit.Builder()
      .baseUrl(ApiConstants.BASE_ENDPOINT)
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create())
      .build()
      .create(PetFinderApi::class.java)
  }

  @Provides
  fun provideOkHttpClient(
    httpLoggingInterceptor: HttpLoggingInterceptor,
    authenticationInterceptor: AuthenticationInterceptor
  ): OkHttpClient {
    val hostname = "**.petfinder.com" //Double-asterisk matches any number of subdomains.
    val certificatePinner = CertificatePinner.Builder()
      .add(hostname, "sha256/JSMzqOOrtyOT1kmau6zKhgT676hGgczD5VMdRMyJZFA=")
      .add(hostname, "sha256/jx6sz/faeVkZtFfGl9r3BIxIkhOKqffMsK0iEi+1FX8=")
      .build()

    val ctInterceptor = certificateTransparencyInterceptor {
      // Enable for the provided hosts
      +"*.petfinder.com" // For subdomains
      +"petfinder.com" // Double asterisk does not cover base domain
      //+"*.*" - this will add all hosts
      //-"legacy.petfinder.com" // Exclude specific hosts
    }

    return OkHttpClient.Builder()
      .certificatePinner(certificatePinner)
      .addNetworkInterceptor(ctInterceptor)
      .addInterceptor(authenticationInterceptor)
      .addInterceptor(httpLoggingInterceptor)
      .cache(null)
      .build()
  }

  @Provides
  fun provideHttpLoggingInterceptor(loggingInterceptor: LoggingInterceptor): HttpLoggingInterceptor {
    val interceptor = HttpLoggingInterceptor(loggingInterceptor)

    interceptor.level = HttpLoggingInterceptor.Level.BODY

    return interceptor
  }
}