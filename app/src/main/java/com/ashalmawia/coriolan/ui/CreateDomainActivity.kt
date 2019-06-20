package com.ashalmawia.coriolan.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.DomainsRegistry
import com.ashalmawia.coriolan.data.backup.ui.RestoreFromBackupActivity
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.DataProcessingException
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.Language
import com.ashalmawia.coriolan.ui.view.visible
import com.ashalmawia.coriolan.util.restartApp
import kotlinx.android.synthetic.main.create_domain.*
import java.lang.Exception

private const val EXTRA_FIRST_START = "cancellable"

class CreateDomainActivity : BaseActivity() {

    private lateinit var preferences: Preferences

    private var firstStart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_domain)

        preferences = Preferences.get(this)

        firstStart = intent.getBooleanExtra(EXTRA_FIRST_START, false)
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
            buttonCancel.visibility = View.GONE
            buttonOk.setText(R.string.button_next)

            welcomeLabelTitle.visibility = View.VISIBLE
            welcomeLabelSubtitle.visibility = View.VISIBLE
        } else {
            buttonCancel.setOnClickListener { finish() }
            buttonOk.setText(R.string.button_create)

            welcomeLabelTitle.visibility = View.GONE
            welcomeLabelSubtitle.visibility = View.GONE
        }

        initializeWithLastTranslationsLanguage()

        buttonOk.setOnClickListener { verifyAndSave() }
    }

    private fun initializeWithLastTranslationsLanguage() {
        val lastTranslationsLanguageId = preferences.getLastTranslationsLanguageId()
        if (lastTranslationsLanguageId != null) {
            val language = Repository.get(this).languageById(lastTranslationsLanguageId)!!
            prefillTranslationsLanguage(language)
        }
    }

    private fun prefillTranslationsLanguage(language: Language) {
        inputTranslationsLang.apply {
            setText(language.value)
            isEnabled = false
        }
        buttonChangeTranslationsLang.apply {
            visible = true
            setOnClickListener {
                inputTranslationsLang.isEnabled = true
                inputTranslationsLang.requestFocus()
            }
        }
    }

    private fun verify(originalLang: String, translationsLang: String): Boolean {
        if (TextUtils.isEmpty(originalLang)) {
            showError(R.string.create_domain__verify__empty_original)
            return false
        }
        if (TextUtils.isEmpty(translationsLang)) {
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
        val originalLang = inputOriginalLang.text.toString()
        val translationsLang = inputTranslationsLang.text.toString()

        if (verify(originalLang, translationsLang)) {
            createDomain(originalLang, translationsLang)
        }
    }

    private fun createDomain(originalLang: String, translationsLang: String) {
        try {
            val domain = DomainsRegistry.get(this).createDomain(originalLang, translationsLang)
            showMessage(R.string.create_domain__created)
            preferences.setLastTranslationsLanguageId(domain.langTranslations())
            if (firstStart) {
                // do not go to domain details directly on the first start
                // as we want to configure backstack properly
                restartApp()
            } else {
                openDomainActivity(domain)
            }
            finish()
        } catch (e: DataProcessingException) {
            showError(getString(R.string.create_domain__error__already_exists, originalLang, translationsLang))
        } catch (e: Exception) {
            showError(R.string.create_domain__error__generic)
        }
    }

    private fun openDomainActivity(domain: Domain) {
        val intent = DomainActivity.intent(this, domain)
        startActivity(intent)
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
            val intent = Intent(context, CreateDomainActivity::class.java)
            intent.putExtra(EXTRA_FIRST_START, firstStart)
            return intent
        }
    }
}