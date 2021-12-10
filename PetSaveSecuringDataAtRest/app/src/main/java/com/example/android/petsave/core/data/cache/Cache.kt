

package com.example.android.petsave.core.data.cache

import com.example.android.petsave.core.data.cache.model.cachedanimal.CachedAnimalAggregate
import com.example.android.petsave.core.data.cache.model.cachedorganization.CachedOrganization
import com.example.android.petsave.core.domain.model.animal.Animal
import io.reactivex.Flowable

interface Cache {
  fun getNearbyAnimals(): Flowable<List<CachedAnimalAggregate>>
  fun storeOrganizations(organizations: List<CachedOrganization>)
  fun storeNearbyAnimals(animals: List<CachedAnimalAggregate>)
  suspend fun getAllTypes(): List<String>

  fun searchAnimalsBy(
      nameOrBreed: String,
      age: String,
      type: String
  ): Flowable<List<CachedAnimalAggregate>>
}