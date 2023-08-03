package com.ashalmawia.coriolan.ui.domain_add_edit

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.TaskStackBuilder
import androidx.core.view.isVisible
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.AddEditDomainBinding
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.Language
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.add_edit.AddEditCardActivity
import com.ashalmawia.coriolan.ui.main.DomainActivity
import com.ashalmawia.coriolan.ui.view.visible

interface AddEditDomainView {
    val context: Context
    fun initalize(isFirstStart: Boolean)
    fun prefillTranslationsLanguage(language: Language)
    fun showError(@StringRes messageId: Int)
    fun showError(message: String)
    fun showMessage(@StringRes messageId: Int)
    fun openAddCardsActivity(domain: Domain, defaultDeck: Deck)
    fun finish()
}

class AddEditDomainViewImpl(
        private val views: AddEditDomainBinding,
        private val activity: BaseActivity,
        private val viewModel: AddEditDomainViewModel
) : AddEditDomainView {

    override val context: Context
        get() = activity

    override fun initalize(isFirstStart: Boolean) {
        if (isFirstStart) {
            // show logo and don't allow to cancel this activity
            activity.setUpToolbarWithLogo()
        } else {
            activity.setUpToolbar(R.string.create_domain__title)
        }

        initialize(isFirstStart)
    }

    private fun initialize(isFirstStart: Boolean) {
        if (isFirstStart) {
            // if it's the first start, user can't cancel this activity
            views.buttonCancel.isVisible = false
            views.buttonOk.setText(R.string.button_next)

            views.welcomeLabelTitle.isVisible = true
            views.welcomeLabelSubtitle.isVisible = true
        } else {
            views.buttonCancel.setOnClickListener { activity.finish() }
            views.buttonOk.setText(R.string.button_create)

            views.welcomeLabelTitle.isVisible = false
            views.welcomeLabelSubtitle.isVisible = false
        }

        views.buttonOk.setOnClickListener {
            val originalLang = views.inputOriginalLang.text.toString()
            val translationsLang = views.inputTranslationsLang.text.toString()
            viewModel.verifyAndSave(originalLang, translationsLang)
        }
    }

    override fun prefillTranslationsLanguage(language: Language) {
        views.inputTranslationsLang.apply {
            setText(language.value)
            isEnabled = false
        }
        views.buttonChangeTranslationsLang.apply {
            visible = true
            setOnClickListener {
                views.inputTranslationsLang.isEnabled = true
                views.inputTranslationsLang.requestFocus()
            }
        }
    }

    override fun showError(@StringRes messageId: Int) {
        showMessage(messageId)
    }

    override fun showError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun showMessage(@StringRes messageId: Int) {
        Toast.makeText(activity, messageId, Toast.LENGTH_SHORT).show()
    }

    override fun openAddCardsActivity(domain: Domain, defaultDeck: Deck) {
        TaskStackBuilder.create(activity)
                .addNextIntent(DomainActivity.intent(activity, domain))
                .addNextIntent(AddEditCardActivity.add(activity, defaultDeck))
                .startActivities()
    }

    override fun finish() {
        activity.finish()
    }
}