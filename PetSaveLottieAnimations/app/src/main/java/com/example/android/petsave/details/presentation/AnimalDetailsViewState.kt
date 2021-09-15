package com.example.android.petsave.details.presentation

import com.example.android.petsave.core.presentation.model.UIAnimalDetailed

sealed class AnimalDetailsViewState {
    object Loading : AnimalDetailsViewState()

    data class AnimalDetails(
        val animal: UIAnimalDetailed
    ) : AnimalDetailsViewState()

    object Failure : AnimalDetailsViewState()
}