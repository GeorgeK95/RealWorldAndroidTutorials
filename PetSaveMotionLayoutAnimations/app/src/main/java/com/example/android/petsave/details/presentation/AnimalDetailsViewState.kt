package com.example.android.petsave.details.presentation

import com.example.android.petsave.common.presentation.model.UIAnimalDetailed

sealed class AnimalDetailsViewState {
    object Loading : AnimalDetailsViewState()

    data class AnimalDetails(
        val animal: UIAnimalDetailed,
        val adopted: Boolean = false
    ) : AnimalDetailsViewState()

    object Failure : AnimalDetailsViewState()
}