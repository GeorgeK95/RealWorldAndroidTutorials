package com.example.android.petsave.onboarding.presentation

sealed class OnboardingViewEffect {
    object NavigateToAnimalsNearYou : OnboardingViewEffect()
}