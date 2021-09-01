package com.example.petsave.animalsnearyou.presentation

import com.example.petsave.common.presentation.Event
import com.example.petsave.common.presentation.model.UIAnimal

data class AnimalsNearYouViewState(
    val loading: Boolean = true,
    val animals: List<UIAnimal> = emptyList(),
    val noMoreAnimalsNearby: Boolean = false,
    val failure: Event<Throwable>? = null
)