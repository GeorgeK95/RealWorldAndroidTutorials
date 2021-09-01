package com.example.petsave.animalsnearyou.domain.usecases

import com.example.petsave.common.domain.repositories.AnimalRepository
import javax.inject.Inject

class GetAnimals @Inject constructor(private val animalRepository: AnimalRepository) {
    operator fun invoke() = animalRepository.getAnimals().filter { it.isNotEmpty() }
}