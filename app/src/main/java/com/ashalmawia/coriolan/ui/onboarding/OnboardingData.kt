package com.ashalmawia.coriolan.ui.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ashalmawia.coriolan.R

data class OnboardingData(
        @DrawableRes val imageRes: Int,
        @StringRes val titleText: Int,
        @StringRes val descriptionText: Int
) {

    companion object {

        fun byNumber(screenNumber: Int): OnboardingData {
            return  when (screenNumber) {
                1 -> OnboardingData(
                        R.drawable.onboarding_1_create_card,
                        R.string.onboarding_1_title,
                        R.string.onboarding_1_description
                )
                2 -> OnboardingData(
                        R.drawable.onboarding_2_learn_card,
                        R.string.onboarding_2_title,
                        R.string.onboarding_2_description
                )
                3 -> OnboardingData(
                        R.drawable.onboarding_3_evaluate,
                        R.string.onboarding_3_title,
                        R.string.onboarding_3_description
                )
                4 -> OnboardingData(
                        R.drawable.onboarding_4_spaced_rep,
                        R.string.onboarding_4_title,
                        R.string.onboarding_4_description
                )
                5 -> OnboardingData(
                        R.drawable.onboarding_5_limits,
                        R.string.onboarding_5_title,
                        R.string.onboarding_5_description
                )
                else -> throw IllegalArgumentException("unexpected onboarding screen number: $screenNumber")
            }
        }
    }

}