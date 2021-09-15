package com.example.android.petsave.details.domain.usecases

import com.example.android.petsave.core.domain.model.animal.AnimalWithDetails
import com.example.android.petsave.core.domain.repositories.AnimalRepository
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AnimalDetails @Inject constructor(
    private val animalRepository: AnimalRepository
) {

    operator fun invoke(
        animalId: Long
    ): Single<AnimalWithDetails> {
        return animalRepository.getAnimal(animalId).delay(2, TimeUnit.SECONDS)
    }
}