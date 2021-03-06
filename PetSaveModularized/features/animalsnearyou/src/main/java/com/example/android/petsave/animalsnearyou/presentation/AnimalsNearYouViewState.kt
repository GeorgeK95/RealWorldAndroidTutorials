

package com.example.android.petsave.animalsnearyou.presentation

import com.example.android.petsave.common.presentation.Event
import com.example.android.petsave.common.presentation.model.UIAnimal

data class AnimalsNearYouViewState(
    val loading: Boolean = true,
    val animals: List<UIAnimal> = emptyList(),
    val noMoreAnimalsNearby: Boolean = false,
    val failure: Event<Throwable>? = null
)