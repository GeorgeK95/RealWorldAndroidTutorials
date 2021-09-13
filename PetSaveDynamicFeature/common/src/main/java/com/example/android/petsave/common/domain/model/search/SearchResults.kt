package com.example.android.petsave.common.domain.model.search

import com.example.android.petsave.common.domain.model.animal.Animal

data class SearchResults(
    val animals: List<Animal>,
    val searchParameters: SearchParameters
)