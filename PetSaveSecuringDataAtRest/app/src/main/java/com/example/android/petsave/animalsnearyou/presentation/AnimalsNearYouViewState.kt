

package com.example.android.petsave.animalsnearyou.presentation

import com.example.android.petsave.core.presentation.model.UIAnimal
import com.example.android.petsave.core.presentation.Event
import java.lang.Exception

data class AnimalsNearYouViewState(
    val loading: Boolean = true,
    val animals: List<UIAnimal> = emptyList(),
    val noMoreAnimalsNearby: Boolean = false,
    val failure: Event<Throwable>? = null
)