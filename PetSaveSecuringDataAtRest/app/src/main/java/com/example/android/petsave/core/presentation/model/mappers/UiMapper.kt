

package com.example.android.petsave.core.presentation.model.mappers

interface UiMapper<E, V> {

  fun mapToView(input: E): V
}