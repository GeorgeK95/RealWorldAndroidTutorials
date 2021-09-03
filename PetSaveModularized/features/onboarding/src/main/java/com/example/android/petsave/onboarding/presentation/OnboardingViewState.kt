package com.example.android.petsave.onboarding.presentation

import androidx.annotation.StringRes
import com.example.android.petsave.onboarding.R

data class OnboardingViewState(
    val postcode: String = "",
    val distance: String = "",
    @StringRes val postcodeError: Int = R.string.no_error,
    @StringRes val distanceError: Int = R.string.no_error
) {
    val submitButtonActive: Boolean
        get() {
            return postcode.isNotEmpty() &&
                    distance.isNotEmpty() &&
                    postcodeError == R.string.no_error &&
                    distanceError == R.string.no_error
        }
}