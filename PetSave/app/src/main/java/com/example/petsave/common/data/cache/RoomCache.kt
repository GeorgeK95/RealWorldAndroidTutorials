package com.example.petsave.common.data.cache

import com.example.petsave.common.data.cache.daos.AnimalsDao
import com.example.petsave.common.data.cache.daos.OrganizationsDao
import com.example.petsave.common.data.cache.model.cachedanimal.CachedAnimalAggregate
import com.example.petsave.common.data.cache.model.cachedorganization.CachedOrganization
import io.reactivex.Flowable
import javax.inject.Inject

class RoomCache @Inject constructor(
    private val animalsDao: AnimalsDao,
    private val organizationsDao: OrganizationsDao
) : Cache {
    override fun getNearbyAnimals(): Flowable<List<CachedAnimalAggregate>> =
        animalsDao.getAllAnimals()

    override suspend fun storeNearbyAnimals(animals: List<CachedAnimalAggregate>) {
        animalsDao.insertAnimalsWithDetails(animals)
    }

    override suspend fun storeOrganizations(organizations: List<CachedOrganization>) {
        organizationsDao.insert(organizations)
    }

    override fun searchAnimalsBy(
        name: String,
        age: String,
        type: String
    ): Flowable<List<CachedAnimalAggregate>> {
        return animalsDao.searchAnimalsBy(name, age, type)
    }

    override fun getAllTypes(): List<String> {
        return animalsDao.getAllTypes()
    }
}