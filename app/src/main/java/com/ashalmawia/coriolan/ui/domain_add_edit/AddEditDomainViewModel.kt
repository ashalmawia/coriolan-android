package com.ashalmawia.coriolan.ui.domain_add_edit

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.DomainsRegistry
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.DataProcessingException
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain

private const val TAG = "AddEditDomainActivity"

class AddEditDomainViewModel(
        private val repository: Repository,
        private val preferences: Preferences,
        private val domainsRegistry: DomainsRegistry,
        private val isFirstStart: Boolean,
        private val domainId: Long?
) : ViewModel() {

    private val domain: Domain? by lazy { if (domainId != null) repository.domainById(domainId) else null }
    private val isEditing: Boolean = domainId != null

    private var view: AddEditDomainView? = null

    fun start(view: AddEditDomainView) {
        this.view = view
        if (isEditing) {
            view.initalizeForEditing(domain!!)
        } else {
            view.initalizeForCreation(isFirstStart)
            initializeWithLastTranslationsLanguage()
        }
    }

    fun stop() {
        this.view = null
    }

    fun verifyAndSave(domainData: DomainData) {
        if (!verify(domainData)) return

        if (isEditing) {
            updateDomain(domainData)
        } else {
            createDomain(domainData)
        }
    }

    private fun initializeWithLastTranslationsLanguage() {
        val lastTranslationsLanguageId = preferences.getLastTranslationsLanguageId()
        if (lastTranslationsLanguageId != null) {
            val language = repository.languageById(lastTranslationsLanguageId)!!
            view?.prefillTranslationsLanguage(language)
        }
    }

    private fun createDomain(data: DomainData) {
        val view = view ?: return
        val defaultDeckName = view.context.getString(R.string.decks_default)

        try {
            val (domain, defaultDeck) = domainsRegistry.createDomain(
                    originalLangName = data.langOriginal,
                    translationsLangName = data.langTranslation,
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

    private fun updateDomain(data: DomainData) {
        val view = view ?: return

        try {
            domainsRegistry.updateDomain(
                    domain = domain!!,
                    originalLangName = data.langOriginal,
                    translationsLangName = data.langTranslation
            )
            view.showMessage(R.string.create_domain__created)
            view.finish()
        } catch (e: DataProcessingException) {
            view.showError(R.string.create_domain__error__already_exists)
            Log.e(TAG, "failed to update domain", e)
        } catch (e: Exception) {
            view.showError(R.string.update_domain__error__generic)
            Log.e(TAG, "failed to update domain", e)
        }
    }

    private fun verify(data: DomainData): Boolean {
        val view = view ?: return false

        if (data.langOriginal.isBlank()) {
            view.showError(R.string.create_domain__verify__empty_original)
            return false
        }
        if (data.langTranslation.isBlank()) {
            view.showError(R.string.create_domain__verify__empty_translations)
            return false
        }

        return true
    }

    data class DomainData(val langOriginal: String, val langTranslation: String)

}