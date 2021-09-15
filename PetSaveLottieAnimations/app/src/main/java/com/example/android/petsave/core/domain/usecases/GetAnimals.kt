package com.example.android.petsave.core.domain.usecases

import com.example.android.petsave.core.domain.repositories.AnimalRepository
import javax.inject.Inject

class GetAnimals @Inject constructor(
    private val animalRepository: AnimalRepository
) {

    operator fun invoke() = animalRepository.getAnimals()
        .filter { it.isNotEmpty() }
}