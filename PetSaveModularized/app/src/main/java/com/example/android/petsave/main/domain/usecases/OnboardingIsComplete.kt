

package com.example.android.petsave.main.domain.usecases

import com.example.android.petsave.common.domain.repositories.AnimalRepository
import javax.inject.Inject

class OnboardingIsComplete @Inject constructor(
    private val repository: AnimalRepository
) {
    suspend operator fun invoke() = repository.onboardingIsComplete()
}
