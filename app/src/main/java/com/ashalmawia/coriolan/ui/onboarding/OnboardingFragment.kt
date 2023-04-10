package com.ashalmawia.coriolan.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ashalmawia.coriolan.databinding.OnboardingScreenBinding
import com.ashalmawia.coriolan.ui.BaseFragment

private const val ARGUMENT_SCREEN_NUMBER = "screen_number"

class OnboardingFragment : BaseFragment() {

    companion object {
        fun createFragment(screenNumber: Int): OnboardingFragment {
            return OnboardingFragment().also { it.arguments = createArguments(screenNumber) }
        }

        private fun createArguments(screenNumber: Int): Bundle {
            return Bundle().also { it.putInt(ARGUMENT_SCREEN_NUMBER, screenNumber) }
        }
    }

    private lateinit var views: OnboardingScreenBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        views = OnboardingScreenBinding.inflate(inflater, container, false)
        return views.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val screnNumber = requireArguments().getInt(ARGUMENT_SCREEN_NUMBER)
        val data = OnboardingData.byNumber(screnNumber)
        views.bindData(data)
    }

    private fun OnboardingScreenBinding.bindData(data: OnboardingData) {
        onboardingImageView.setImageResource(data.imageRes)
        onboardingTitleView.setText(data.titleText)
        onboardingDetailsView.setText(data.descriptionText)
    }
}