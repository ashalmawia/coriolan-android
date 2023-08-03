package com.ashalmawia.coriolan.ui.domain_add_edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.StringRes
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.TaskStackBuilder
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.DomainsRegistry
import com.ashalmawia.coriolan.ui.backup.RestoreFromBackupActivity
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.DataProcessingException
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.AddEditDomainBinding
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.Language
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.add_edit.AddEditCardActivity
import com.ashalmawia.coriolan.ui.main.DomainActivity
import com.ashalmawia.coriolan.ui.view.visible
import org.koin.android.ext.android.inject

private const val TAG = "AddEditDomainActivity"

private const val EXTRA_FIRST_START = "cancellable"

class AddEditDomainActivity : BaseActivity() {

    private val views by lazy { AddEditDomainBinding.inflate(layoutInflater) }

    private val preferences: Preferences by inject()
    private val repository: Repository by inject()
    private val domainsRegistry: DomainsRegistry by inject()

    private val firstStart by lazy { intent.getBooleanExtra(EXTRA_FIRST_START, false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)

        if (firstStart) {
            // show logo and don't allow to cancel this activity
            setUpToolbarWithLogo()
        } else {
            setUpToolbar(R.string.create_domain__title)
        }

        initialize(firstStart)
    }

    private fun initialize(firstStart: Boolean) {
        if (firstStart) {
            // if it's the first start, user can't cancel this activity
            views.buttonCancel.visibility = View.GONE
            views.buttonOk.setText(R.string.button_next)

            views.welcomeLabelTitle.visibility = View.VISIBLE
            views.welcomeLabelSubtitle.visibility = View.VISIBLE
        } else {
            views.buttonCancel.setOnClickListener { finish() }
            views.buttonOk.setText(R.string.button_create)

            views.welcomeLabelTitle.visibility = View.GONE
            views.welcomeLabelSubtitle.visibility = View.GONE
        }

        initializeWithLastTranslationsLanguage()

        views.buttonOk.setOnClickListener { verifyAndSave() }
    }

    private fun initializeWithLastTranslationsLanguage() {
        val lastTranslationsLanguageId = preferences.getLastTranslationsLanguageId()
        if (lastTranslationsLanguageId != null) {
            val language = repository.languageById(lastTranslationsLanguageId)!!
            prefillTranslationsLanguage(language)
        }
    }

    private fun prefillTranslationsLanguage(language: Language) {
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

    private fun verify(originalLang: String, translationsLang: String): Boolean {
        if (originalLang.isBlank()) {
            showError(R.string.create_domain__verify__empty_original)
            return false
        }
        if (translationsLang.isBlank()) {
            showError(R.string.create_domain__verify__empty_translations)
            return false
        }

        return true
    }

    private fun showError(@StringRes messageId: Int) {
        showMessage(messageId)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showMessage(@StringRes messageId: Int) {
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
    }

    private fun verifyAndSave() {
        val originalLang = views.inputOriginalLang.text.toString()
        val translationsLang = views.inputTranslationsLang.text.toString()

        if (verify(originalLang, translationsLang)) {
            createDomain(originalLang, translationsLang)
        }
    }

    private fun createDomain(originalLang: String, translationsLang: String) {
        try {
            val (domain, defaultDeck) = domainsRegistry.createDomain(
                    originalLangName = originalLang,
                    translationsLangName = translationsLang,
                    defaultDeckName = defaultDeckName()
            )
            showMessage(R.string.create_domain__created)
            preferences.setLastTranslationsLanguageId(domain.langTranslations())
            openAddCardsActivity(domain, defaultDeck)
            finish()
        } catch (e: DataProcessingException) {
            showError(getString(R.string.create_domain__error__already_exists))
            Log.e(TAG, "failed to create domain", e)
        } catch (e: Exception) {
            showError(R.string.create_domain__error__generic)
            Log.e(TAG, "failed to create domain", e)
        }
    }

    private fun defaultDeckName(): String {
        return getString(R.string.decks_default)
    }

    private fun openAddCardsActivity(domain: Domain, defaultDeck: Deck) {
        TaskStackBuilder.create(this)
                .addNextIntent(DomainActivity.intent(this, domain))
                .addNextIntent(AddEditCardActivity.add(this, defaultDeck))
                .startActivities()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (firstStart) {
            menuInflater.inflate(R.menu.create_domain, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.create_domain_menu__restore_from_backup -> {
                restoreFromBackup()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun restoreFromBackup() {
        val intent = RestoreFromBackupActivity.intent(this)
        startActivity(intent)
    }

    companion object {

        fun intent(context: Context, firstStart: Boolean): Intent {
            val intent = Intent(context, AddEditDomainActivity::class.java)
            intent.putExtra(EXTRA_FIRST_START, firstStart)
            return intent
        }
    }
}