package com.example.petsave.search.usecases

import com.example.petsave.common.domain.model.animal.details.Age
import com.example.petsave.common.domain.repositories.AnimalRepository
import com.example.petsave.search.domain.model.SearchFilters
import java.util.*
import javax.inject.Inject

class GetSearchFilters @Inject constructor(
    private val animalRepository: AnimalRepository
) {

    companion object {
        const val NO_FILTER_SELECTED = "Any"
    }

    suspend operator fun invoke(): SearchFilters {
        val unknown = Age.UNKNOWN.name

        val types = listOf(NO_FILTER_SELECTED) + animalRepository.getAnimalTypes()

        val ages = animalRepository.getAnimalAges()
            .map {
                if (it.name == unknown) {
                    NO_FILTER_SELECTED
                } else {
                    it.name.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
                }
            }

        return SearchFilters(ages, types)
    }
}