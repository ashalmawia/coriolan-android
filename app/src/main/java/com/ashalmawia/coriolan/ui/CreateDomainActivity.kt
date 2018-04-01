package com.ashalmawia.coriolan.ui

import android.app.Activity
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
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain
import kotlinx.android.synthetic.main.create_domain.*

private const val EXTRA_FIRST_START = "cancellable"

private const val REQUEST_CODE_CLOSE = 1

class CreateDomainActivity : BaseActivity() {

    private var firstStart = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_domain)

        firstStart = intent.getBooleanExtra(EXTRA_FIRST_START, true)
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

        buttonOk.setOnClickListener { verifyAndSave() }
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
        val domain = DomainsRegistry.createDomain(Repository.get(this), originalLang, translationsLang)
        if (domain != null) {
            showMessage(R.string.create_domain__created)
            openDomainActivity(domain)
        } else {
            showError(R.string.create_domain__error__already_exists)
        }
    }

    private fun openDomainActivity(domain: Domain) {
        val intent = DomainActivity.intent(this, domain)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (firstStart) {
            menuInflater.inflate(R.menu.create_domain, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.create_domain_menu__restore_from_backup -> {
                restoreFromBackup()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun restoreFromBackup() {
        val intent = RestoreFromBackupActivity.intent(this)
        startActivityForResult(intent, REQUEST_CODE_CLOSE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_CLOSE -> {
                if (resultCode == Activity.RESULT_OK) {
                    restart()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun restart() {
        val intent = Intent(this, StartActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    companion object {

        fun intent(context: Context, firstStart: Boolean): Intent {
            val intent = Intent(context, CreateDomainActivity::class.java)
            intent.putExtra(EXTRA_FIRST_START, firstStart)
            return intent
        }
    }
}