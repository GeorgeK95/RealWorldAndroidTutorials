package com.example.android.petsave.details.domain.usecases

import com.example.android.petsave.common.domain.model.animal.details.AnimalWithDetails
import com.example.android.petsave.common.domain.repositories.AnimalRepository
import io.reactivex.Single
import javax.inject.Inject

class AnimalDetails @Inject constructor(
    private val animalRepository: AnimalRepository
) {

    operator fun invoke(
        animalId: Long
    ): Single<AnimalWithDetails> {
        return animalRepository.getAnimal(animalId)
    }
}