package com.example.petsave.common.domain.repositories

import com.example.petsave.common.domain.model.animal.Animal
import com.example.petsave.common.domain.model.animal.details.Age
import com.example.petsave.common.domain.model.animal.details.AnimalWithDetails
import com.example.petsave.common.domain.model.pagination.PaginatedAnimals
import com.example.petsave.common.domain.model.pagination.Pagination
import com.example.petsave.search.domain.model.SearchParameters
import com.example.petsave.search.domain.model.SearchResults
import io.reactivex.Flowable

interface AnimalRepository {
    suspend fun requestMoreAnimals(pageToLoad: Int, numberOfItems: Int): PaginatedAnimals
    suspend fun storeAnimals(animals: List<AnimalWithDetails>)
    fun searchCachedAnimalsBy(parameters: SearchParameters): Flowable<SearchResults>
    suspend fun searchAnimalsRemotely(
        pageToLoad: Int,
        searchParameters: SearchParameters,
        numberOfItems: Int
    ): PaginatedAnimals
    fun getAnimalAges(): List<Age>
    fun getAnimalTypes(): List<String>
    fun getAnimals(): Flowable<List<Animal>>
}
