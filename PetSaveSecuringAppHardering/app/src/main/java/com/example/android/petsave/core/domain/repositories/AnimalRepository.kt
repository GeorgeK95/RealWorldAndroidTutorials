package com.example.android.petsave.core.domain.repositories

import com.example.android.petsave.core.domain.model.animal.Animal
import com.example.android.petsave.core.domain.model.animal.AnimalWithDetails
import com.example.android.petsave.core.domain.model.pagination.PaginatedAnimals
import com.example.android.petsave.search.domain.model.SearchParameters
import com.example.android.petsave.search.domain.model.SearchResults
import io.reactivex.Flowable

interface AnimalRepository {

    fun getAnimals(): Flowable<List<Animal>>
    suspend fun requestMoreAnimals(pageToLoad: Int, numberOfItems: Int): PaginatedAnimals
    suspend fun storeAnimals(animals: List<AnimalWithDetails>)
    suspend fun getAnimalTypes(): List<String>
    fun getAnimalAges(): List<AnimalWithDetails.Details.Age>
    fun searchCachedAnimalsBy(searchParameters: SearchParameters): Flowable<SearchResults>

    suspend fun searchAnimalsRemotely(
        pageToLoad: Int,
        searchParameters: SearchParameters,
        numberOfItems: Int
    ): PaginatedAnimals
}