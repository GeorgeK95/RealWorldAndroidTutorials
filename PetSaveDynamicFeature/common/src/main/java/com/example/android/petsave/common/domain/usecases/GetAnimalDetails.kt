package com.example.android.petsave.common.domain.usecases

import com.example.android.petsave.common.domain.model.animal.details.AnimalWithDetails
import com.example.android.petsave.common.domain.repositories.AnimalRepository
import javax.inject.Inject

class GetAnimalDetails @Inject constructor(
    private val animalRepository: AnimalRepository
) {

    suspend operator fun invoke(animalId: Long): AnimalWithDetails {
        return animalRepository.getAnimal(animalId)
    }
}