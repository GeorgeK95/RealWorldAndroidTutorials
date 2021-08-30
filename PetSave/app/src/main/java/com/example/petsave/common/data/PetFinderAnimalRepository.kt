package com.example.petsave.common.data

import com.example.petsave.common.data.api.PetFinderApi
import com.example.petsave.common.data.api.model.mappers.ApiAnimalMapper
import com.example.petsave.common.data.api.model.mappers.ApiPaginationMapper
import com.example.petsave.common.data.cache.Cache
import com.example.petsave.common.data.cache.model.cachedanimal.CachedAnimalAggregate
import com.example.petsave.common.data.cache.model.cachedorganization.CachedOrganization
import com.example.petsave.common.domain.model.animal.Animal
import com.example.petsave.common.domain.model.animal.details.AnimalWithDetails
import com.example.petsave.common.domain.model.pagination.PaginatedAnimals
import com.example.petsave.common.domain.repositories.AnimalRepository
import io.reactivex.Flowable
import javax.inject.Inject

class PetFinderAnimalRepository @Inject constructor(
    private val api: PetFinderApi,
    private val cache: Cache,
    private val apiAnimalMapper: ApiAnimalMapper,
    private val apiPaginationMapper: ApiPaginationMapper
) : AnimalRepository {

    override fun getAnimals(): Flowable<List<Animal>> {
        return cache.getNearbyAnimals()
            .distinctUntilChanged()
            .map { animalList ->
                animalList.map {
                    it.animal.toAnimalDomain(it.photos, it.videos, it.tags)
                }
            }
    }

    override suspend fun requestMoreAnimals(pageToLoad: Int, numberOfItems: Int): PaginatedAnimals {
        val (apiAnimals, apiPagination) = api.getNearbyAnimals(
            pageToLoad,
            numberOfItems,
            postcode,
            maxDistanceMiles
        )

        return PaginatedAnimals(
            apiAnimals?.map {
                apiAnimalMapper.mapToDomain(it)
            }.orEmpty(),
            apiPaginationMapper.mapToDomain(apiPagination)
        )
    }

    override suspend fun storeAnimals(animals: List<AnimalWithDetails>) {
        val organizations = animals.map { CachedOrganization.fromDomain(it.details.organization) }

        cache.storeOrganizations(organizations)
        cache.storeNearbyAnimals(animals.map { CachedAnimalAggregate.fromDomain(it) })
    }

    private val postcode = "07097"
    private val maxDistanceMiles = 100

}