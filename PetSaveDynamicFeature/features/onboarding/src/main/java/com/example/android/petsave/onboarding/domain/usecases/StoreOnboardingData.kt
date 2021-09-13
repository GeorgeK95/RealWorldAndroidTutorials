package com.example.android.petsave.onboarding.domain.usecases

import com.example.android.petsave.common.domain.repositories.AnimalRepository
import javax.inject.Inject

class StoreOnboardingData @Inject constructor(
    private val repository: AnimalRepository
) {

    suspend operator fun invoke(postcode: String, distance: String) {
        repository.storeOnboardingData(postcode, distance.toInt())
    }
}
