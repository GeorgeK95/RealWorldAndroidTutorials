package com.example.petsave.common.data.di

import com.example.petsave.common.data.cache.PetSaveDatabase
import com.example.petsave.common.data.preferences.PetSavePreferences
import com.example.petsave.common.data.preferences.Preferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ApplicationComponent::class)
abstract class PreferencesModule {
    @Binds
    abstract fun providePreferences(preferences: PetSavePreferences): Preferences
}