package com.ashalmawia.coriolan.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.core.view.forEachIndexed
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.CreateDomainActivity
import com.ashalmawia.coriolan.ui.view.visible
import kotlinx.android.synthetic.main.onboarding_activity.onboardingButtonDone
import kotlinx.android.synthetic.main.onboarding_activity.onboardingButtonNext
import kotlinx.android.synthetic.main.onboarding_activity.onboardingButtonSkip
import kotlinx.android.synthetic.main.onboarding_activity.onboardingIndicatorsContainer
import kotlinx.android.synthetic.main.onboarding_activity.onboardingSlider
import org.koin.android.ext.android.inject

private const val LAST_PAGE_INDEX = 4

class OnboardingActivity : BaseActivity() {

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, OnboardingActivity::class.java)
        }
    }

    private val preferences: Preferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_activity)
        onboardingSlider.adapter = OnboardingAdapter(this)
        onboardingSlider.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                this@OnboardingActivity.onPageSelected(position)
            }
        })
        onboardingButtonSkip.setOnClickListener { onOnboardingCompleted() }
        onboardingButtonDone.setOnClickListener { onOnboardingCompleted() }
        onboardingButtonNext.setOnClickListener { onboardingSlider.currentItem = onboardingSlider.currentItem + 1 }
    }

    private fun onPageSelected(page: Int) {
        updatePageIndicator(page)

        val isLastPage = page == LAST_PAGE_INDEX
        onboardingButtonDone.visible = isLastPage
        onboardingButtonNext.visible = !isLastPage
        onboardingButtonSkip.visibility = if (isLastPage) INVISIBLE else VISIBLE
    }

    private fun updatePageIndicator(selectedPage: Int) {
        onboardingIndicatorsContainer.forEachIndexed { index, view ->
            view.isSelected = index == selectedPage
        }
    }

    private fun onOnboardingCompleted() {
        preferences.recordOnboardingCompleted()
        openDomainCreation()
    }

    private fun openDomainCreation() {
        val intent = CreateDomainActivity.intent(this, true)
        startActivity(intent)
    }
}