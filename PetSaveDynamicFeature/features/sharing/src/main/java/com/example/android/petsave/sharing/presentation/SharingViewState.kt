package com.example.android.petsave.sharing.presentation

import com.example.android.petsave.sharing.presentation.model.UIAnimalToShare

data class SharingViewState(
    val animalToShare: UIAnimalToShare = UIAnimalToShare(image = "", defaultMessage = "")
)