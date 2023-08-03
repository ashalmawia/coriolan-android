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
import com.ashalmawia.coriolan.ui.domain_add_edit.AddEditDomainViewModel.DomainData

interface AddEditDomainView {
    val context: Context
    fun initalizeForCreation(isFirstStart: Boolean)
    fun initalizeForEditing(domain: Domain)
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

    override fun initalizeForCreation(isFirstStart: Boolean) {
        if (isFirstStart) {
            // show logo and don't allow to cancel this activity
            activity.setUpToolbarWithLogo()
        } else {
            activity.setUpToolbar(R.string.create_domain__title)
        }

        initialize(
                showWelcome = isFirstStart,
                allowCancel = !isFirstStart,
                confirmButtonTitle = if (isFirstStart) R.string.button_next else R.string.button_create
        )
    }

    override fun initalizeForEditing(domain: Domain) {
        activity.setUpToolbar(R.string.edit_domain__title)
        initialize(
                showWelcome = false,
                allowCancel = true,
                confirmButtonTitle = R.string.button_save
        )
        views.apply {
            inputOriginalLang.setText(domain.langOriginal().value)
            inputTranslationsLang.setText(domain.langTranslations().value)
        }
    }

    private fun initialize(
            showWelcome: Boolean, allowCancel: Boolean,
            @StringRes confirmButtonTitle: Int
    ) {
        views.apply {
            buttonCancel.setOnClickListener { activity.finish() }
            buttonCancel.isVisible = allowCancel

            buttonOk.setText(confirmButtonTitle)
            buttonOk.setOnClickListener {
                val data = extractInput()
                viewModel.verifyAndSave(data)
            }

            welcomeLabelTitle.isVisible = showWelcome
            welcomeLabelSubtitle.isVisible = showWelcome
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

    private fun extractInput(): DomainData {
        val originalLang = views.inputOriginalLang.text.toString()
        val translationsLang = views.inputTranslationsLang.text.toString()
        return DomainData(originalLang, translationsLang)
    }
}