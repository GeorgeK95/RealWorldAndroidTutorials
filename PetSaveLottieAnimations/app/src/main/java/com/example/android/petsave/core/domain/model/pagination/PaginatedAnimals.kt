package com.example.android.petsave.core.domain.model.pagination

import com.example.android.petsave.core.domain.model.animal.AnimalWithDetails

data class PaginatedAnimals(
    val animals: List<AnimalWithDetails>,
    val pagination: Pagination
)