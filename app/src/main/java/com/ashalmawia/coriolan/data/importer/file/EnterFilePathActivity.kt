package com.ashalmawia.coriolan.data.importer.file

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import com.ashalmawia.coriolan.BuildConfig
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.importer.DataImportFlow
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.EnterFilePathBinding
import com.ashalmawia.coriolan.dependencies.dataImportScope
import com.ashalmawia.coriolan.model.DeckId
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.DomainId
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.util.isPermissionGranted
import com.ashalmawia.coriolan.ui.util.requireSerializable
import com.ashalmawia.coriolan.ui.util.showStoragePermissionDeniedAlert
import org.koin.android.ext.android.inject
import java.io.File

private const val EXTRA_TEXT = "extra_text"
private const val EXTRA_DECK_ID = "deck_id"
private const val EXTRA_DOMAIN_ID = "domain_id"

private val DEBUG_PREFILL_PATH = BuildConfig.DEBUG

class EnterFilePathActivity : BaseActivity() {

    private val views by lazy { EnterFilePathBinding.inflate(layoutInflater) }

    private val repository: Repository by inject()
    private val domain: Domain by lazy {
        val domainId = intent.requireSerializable<DomainId>(EXTRA_DOMAIN_ID)
        repository.domainById(domainId)!!
    }
    private val importFlow: DataImportFlow by dataImportScope().inject()

    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()) { isGranted ->
        if (isGranted) {
            validateAndSubmitInput()
        } else {
            showStoragePermissionDeniedAlert()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)

        setUpToolbar(R.string.import_from_file)

        initalize()

        restore(savedInstanceState)

        views.buttonCancel.setOnClickListener { cancel() }
        views.buttonSubmit.setOnClickListener { validateAndSubmitInputWithPermissionCheck() }

        maybeSetDebugValues()
    }

    private fun initalize() {
        views.deckSelector.initialize(repository.allDecks(domain))
    }

    private fun cancel() {
        finish()
    }

    private fun validateAndSubmitInputWithPermissionCheck() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (isPermissionGranted(permission)) {
            validateAndSubmitInput()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun validateAndSubmitInput() {
        val path = views.editText.text.toString()
        if (path.isBlank()) {
            showMessage(getString(R.string.import_file_empty_path))
            return
        }

        submit(path)
    }

    private fun submit(path: String) {
        val file = File(path)
        if (!file.exists() || file.isDirectory) {
            val message = getString(R.string.import_file_does_not_exist, path)
            showMessage(message)
            return
        }

        (importFlow.importer as ImporterFromFile).onFile(path, views.deckSelector.selectedDeck())
        finish()
    }

    private fun showMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun restore(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val text = savedInstanceState.getString(EXTRA_TEXT)
            if (text != null) {
                views.editText.setText(text)
            }
            val deckId = savedInstanceState.requireSerializable<DeckId>(EXTRA_DECK_ID)
            views.deckSelector.selectDeckWithId(deckId)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_TEXT, views.editText.text.toString())
        outState.putSerializable(EXTRA_DECK_ID, views.deckSelector.selectedDeck().id)
    }

    companion object {
        fun intent(context: Context, domain: Domain): Intent {
            return Intent(context, EnterFilePathActivity::class.java)
                    .putExtra(EXTRA_DOMAIN_ID, domain.id)
        }
    }

    @SuppressLint("SetTextI18n", "SdCardPath")
    private fun maybeSetDebugValues() {
        if (DEBUG_PREFILL_PATH) {
            views.editText.setText("/mnt/sdcard/import_from_file")
        }
    }
}