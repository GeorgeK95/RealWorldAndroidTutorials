package com.example.android.petsave.sharing.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.petsave.common.data.PetFinderAnimalRepository
import com.example.android.petsave.common.domain.repositories.AnimalRepository
import com.example.android.petsave.common.utils.CoroutineDispatchersProvider
import com.example.android.petsave.common.utils.DispatchersProvider
import com.example.android.petsave.sharing.presentation.SharingFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.migration.DisableInstallInCheck
import dagger.multibindings.IntoMap

@Module
@DisableInstallInCheck
abstract class SharingModule {

    @Binds
    @IntoMap
    @ViewModelKey(SharingFragmentViewModel::class)
    abstract fun bindSharingFragmentViewModel(
        sharingFragmentViewModel: SharingFragmentViewModel
    ): ViewModel

    @Binds
    @Reusable
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    abstract fun bindDispatchersProvider(
        dispatchersProvider: CoroutineDispatchersProvider
    ): DispatchersProvider

    @Binds
    abstract fun bindRepository(
        repository: PetFinderAnimalRepository
    ): AnimalRepository

}