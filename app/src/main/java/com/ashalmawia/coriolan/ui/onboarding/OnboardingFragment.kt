package com.ashalmawia.coriolan.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.ui.BaseFragment
import kotlinx.android.synthetic.main.onboarding_screen.onboardingDetailsView
import kotlinx.android.synthetic.main.onboarding_screen.onboardingImageView
import kotlinx.android.synthetic.main.onboarding_screen.onboardingTitleView

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.onboarding_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val screnNumber = requireArguments().getInt(ARGUMENT_SCREEN_NUMBER)
        val data = OnboardingData.byNumber(screnNumber)
        bindData(data)
    }

    private fun bindData(data: OnboardingData) {
        onboardingImageView.setImageResource(data.imageRes)
        onboardingTitleView.setText(data.titleText)
        onboardingDetailsView.setText(data.descriptionText)
    }
}