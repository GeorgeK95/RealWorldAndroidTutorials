package com.example.petsave.common.data.cache

import com.example.petsave.common.data.cache.model.cachedanimal.CachedAnimalAggregate
import com.example.petsave.common.data.cache.model.cachedorganization.CachedOrganization
import io.reactivex.Flowable

interface Cache {
    fun getNearbyAnimals(): Flowable<List<CachedAnimalAggregate>>
    suspend fun storeOrganizations(organizations: List<CachedOrganization>)
    suspend fun storeNearbyAnimals(animals: List<CachedAnimalAggregate>)
    fun searchAnimalsBy(
        name: String,
        age: String,
        type: String
    ): Flowable<List<CachedAnimalAggregate>>

    fun getAllTypes(): List<String>
}