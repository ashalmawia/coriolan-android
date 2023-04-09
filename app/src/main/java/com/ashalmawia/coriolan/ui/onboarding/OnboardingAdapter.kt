package com.ashalmawia.coriolan.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(activity: OnboardingActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount() = 5

    override fun createFragment(position: Int): Fragment {
        return OnboardingFragment.createFragment(position + 1)
    }
}