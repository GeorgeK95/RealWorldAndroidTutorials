package com.example.android.petsave.common.domain.repositories

import com.example.android.petsave.common.domain.model.animal.Animal
import com.example.android.petsave.common.domain.model.animal.details.Age
import com.example.android.petsave.common.domain.model.animal.details.AnimalWithDetails
import com.example.android.petsave.common.domain.model.pagination.PaginatedAnimals
import com.example.android.petsave.search.domain.model.SearchParameters
import com.example.android.petsave.search.domain.model.SearchResults
import io.reactivex.Flowable
import io.reactivex.Single

interface AnimalRepository {

    fun getAnimals(): Flowable<List<Animal>>
    suspend fun requestMoreAnimals(pageToLoad: Int, numberOfItems: Int): PaginatedAnimals
    suspend fun storeAnimals(animals: List<AnimalWithDetails>)
    suspend fun getAnimalTypes(): List<String>
    fun getAnimalAges(): List<Age>
    fun searchCachedAnimalsBy(searchParameters: SearchParameters): Flowable<SearchResults>

    suspend fun searchAnimalsRemotely(
        pageToLoad: Int,
        searchParameters: SearchParameters,
        numberOfItems: Int
    ): PaginatedAnimals

    fun getAnimal(
        animalId: Long
    ): Single<AnimalWithDetails>
}