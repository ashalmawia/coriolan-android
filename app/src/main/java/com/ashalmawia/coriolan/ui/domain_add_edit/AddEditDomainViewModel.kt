package com.ashalmawia.coriolan.ui.domain_add_edit

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.DomainsRegistry
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.DataProcessingException
import com.ashalmawia.coriolan.data.storage.Repository

private const val TAG = "AddEditDomainActivity"

class AddEditDomainViewModel(
        private val repository: Repository,
        private val preferences: Preferences,
        private val domainsRegistry: DomainsRegistry,
        private val isFirstStart: Boolean
) : ViewModel() {

    private var view: AddEditDomainView? = null

    fun start(view: AddEditDomainView) {
        this.view = view
        view.initalize(isFirstStart)
        initializeWithLastTranslationsLanguage()
    }

    fun stop() {
        this.view = null
    }

    fun verifyAndSave(originalLang: String, translationsLang: String) {
        if (verify(originalLang, translationsLang)) {
            createDomain(originalLang, translationsLang)
        }
    }

    private fun initializeWithLastTranslationsLanguage() {
        val lastTranslationsLanguageId = preferences.getLastTranslationsLanguageId()
        if (lastTranslationsLanguageId != null) {
            val language = repository.languageById(lastTranslationsLanguageId)!!
            view?.prefillTranslationsLanguage(language)
        }
    }

    private fun createDomain(originalLang: String, translationsLang: String) {
        val view = view ?: return
        val defaultDeckName = view.context.getString(R.string.decks_default)

        try {
            val (domain, defaultDeck) = domainsRegistry.createDomain(
                    originalLangName = originalLang,
                    translationsLangName = translationsLang,
                    defaultDeckName = defaultDeckName
            )
            view.showMessage(R.string.create_domain__created)
            preferences.setLastTranslationsLanguageId(domain.langTranslations())
            view.openAddCardsActivity(domain, defaultDeck)
            view.finish()
        } catch (e: DataProcessingException) {
            view.showError(R.string.create_domain__error__already_exists)
            Log.e(TAG, "failed to create domain", e)
        } catch (e: Exception) {
            view.showError(R.string.create_domain__error__generic)
            Log.e(TAG, "failed to create domain", e)
        }
    }

    private fun verify(originalLang: String, translationsLang: String): Boolean {
        val view = view ?: return false

        if (originalLang.isBlank()) {
            view.showError(R.string.create_domain__verify__empty_original)
            return false
        }
        if (translationsLang.isBlank()) {
            view.showError(R.string.create_domain__verify__empty_translations)
            return false
        }

        return true
    }

}