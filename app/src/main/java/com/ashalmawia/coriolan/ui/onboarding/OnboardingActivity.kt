package com.ashalmawia.coriolan.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.core.view.forEachIndexed
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.databinding.OnboardingActivityBinding
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.domain_add_edit.CreateDomainActivity
import com.ashalmawia.coriolan.ui.view.visible
import org.koin.android.ext.android.inject

private const val LAST_PAGE_INDEX = 4

class OnboardingActivity : BaseActivity() {

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, OnboardingActivity::class.java)
        }
    }

    private val views by lazy { OnboardingActivityBinding.inflate(layoutInflater) }

    private val preferences: Preferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        views.setupOnboardingSlider()
    }

    private fun OnboardingActivityBinding.setupOnboardingSlider() {
        onboardingSlider.adapter = OnboardingAdapter(this@OnboardingActivity)
        onboardingSlider.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                renderPageSelected(position)
            }
        })
        onboardingButtonSkip.setOnClickListener { onOnboardingCompleted() }
        onboardingButtonDone.setOnClickListener { onOnboardingCompleted() }
        onboardingButtonNext.setOnClickListener { onboardingSlider.currentItem = onboardingSlider.currentItem + 1 }

    }

    private fun OnboardingActivityBinding.renderPageSelected(page: Int) {
        updatePageIndicator(page)

        val isLastPage = page == LAST_PAGE_INDEX
        onboardingButtonDone.visible = isLastPage
        onboardingButtonNext.visible = !isLastPage
        onboardingButtonSkip.visibility = if (isLastPage) INVISIBLE else VISIBLE
    }

    private fun OnboardingActivityBinding.updatePageIndicator(selectedPage: Int) {
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