package com.example.android.petsave.common.di

import com.example.android.petsave.common.data.PetFinderAnimalRepository
import com.example.android.petsave.common.domain.repositories.AnimalRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import io.reactivex.disposables.CompositeDisposable

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ViewModelsModule {

    @Binds
    abstract fun bindAnimalRepository(repository: PetFinderAnimalRepository): AnimalRepository

    companion object {
        @Provides
        fun provideCompositeDisposable() = CompositeDisposable()
    }
}