package com.example.android.petsave.search.domain.usecases

import com.example.android.petsave.common.domain.model.NoMoreAnimalsException
import com.example.android.petsave.common.domain.model.pagination.Pagination
import com.example.android.petsave.common.domain.model.pagination.Pagination.Companion.DEFAULT_PAGE_SIZE
import com.example.android.petsave.common.domain.model.search.SearchParameters
import com.example.android.petsave.common.domain.repositories.AnimalRepository
import javax.inject.Inject

class SearchAnimalsRemotely @Inject constructor(
    private val animalRepository: AnimalRepository
) {

    suspend operator fun invoke(
        pageToLoad: Int,
        searchParameters: SearchParameters,
        pageSize: Int = DEFAULT_PAGE_SIZE
    ): Pagination {
        val (animals, pagination) =
            animalRepository.searchAnimalsRemotely(pageToLoad, searchParameters, pageSize)

        if (animals.isEmpty()) {
            throw NoMoreAnimalsException("Couldn't find more animals that match the search parameters.")
        }

        animalRepository.storeAnimals(animals)

        return pagination
    }
}