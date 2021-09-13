package com.example.android.petsave.sharing.di

import android.content.Context
import com.example.android.petsave.di.SharingModuleDependencies
import com.example.android.petsave.sharing.presentation.SharingFragment
import dagger.BindsInstance
import dagger.Component

@Component(
    dependencies = [SharingModuleDependencies::class],
    modules = [SharingModule::class]
)
interface SharingComponent {

    fun inject(fragment: SharingFragment)

    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder
        fun moduleDependencies(sharingModuleDependencies: SharingModuleDependencies): Builder
        fun build(): SharingComponent
    }

}