package com.example.android.petsave.common.data.cache.model.cachedorganization

import androidx.room.Embedded
import androidx.room.Relation
import com.example.android.petsave.common.data.cache.model.cachedanimal.CachedAnimalAggregate
import com.example.android.petsave.common.data.cache.model.cachedanimal.CachedAnimalWithDetails

data class CachedOrganizationWithAnimals(
    @Embedded val organization: CachedOrganization,
    @Relation(
        entity = CachedAnimalWithDetails::class,
        parentColumn = "id",
        entityColumn = "organizationId"
    )
    val animals: List<CachedAnimalAggregate>
)