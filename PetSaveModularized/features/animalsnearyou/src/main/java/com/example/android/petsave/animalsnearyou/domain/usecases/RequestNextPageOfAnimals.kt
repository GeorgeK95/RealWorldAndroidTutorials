

package com.example.android.petsave.animalsnearyou.domain.usecases

import com.example.android.petsave.common.domain.model.NoMoreAnimalsException
import com.example.android.petsave.common.domain.model.pagination.Pagination
import com.example.android.petsave.common.domain.repositories.AnimalRepository
import javax.inject.Inject

class RequestNextPageOfAnimals @Inject constructor(private val animalRepository: AnimalRepository) {
    suspend operator fun invoke(
        pageToLoad: Int,
        pageSize: Int = Pagination.DEFAULT_PAGE_SIZE
    ): Pagination {
        val (animals, pagination) = animalRepository.requestMoreAnimals(pageToLoad, pageSize)

        if (animals.isEmpty()) {
            throw NoMoreAnimalsException("No animals nearby :(")
        }

        animalRepository.storeAnimals(animals)

        return pagination
    }
}