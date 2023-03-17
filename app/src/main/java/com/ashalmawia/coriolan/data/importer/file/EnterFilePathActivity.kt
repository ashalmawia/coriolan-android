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
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.importer.DataImportFlow
import com.ashalmawia.coriolan.dependencies.dataImportScope
import com.ashalmawia.coriolan.dependencies.domainScope
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.util.isPermissionGranted
import com.ashalmawia.coriolan.ui.util.showStoragePermissionDeniedAlert
import kotlinx.android.synthetic.main.enter_file_path.*
import java.io.File

const val EXTRA_TEXT = "extra_text"
const val EXTRA_DECK_ID = "deck_id"

private val DEBUG_PREFILL_PATH = BuildConfig.DEBUG

class EnterFilePathActivity : BaseActivity() {

    private val decksRegistry: DecksRegistry by domainScope().inject()
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
        setContentView(R.layout.enter_file_path)

        setUpToolbar(R.string.import_from_file)

        initalize()

        restore(savedInstanceState)

        buttonCancel.setOnClickListener { cancel() }
        buttonSubmit.setOnClickListener { validateAndSubmitInputWithPermissionCheck() }

        maybeSetDebugValues()
    }

    private fun initalize() {
        deckSelector.initialize(decksRegistry.allDecks())
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
        val path = editText.text.toString()
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

        (importFlow.importer as ImporterFromFile).onFile(path, deckSelector.selectedDeck())
        finish()
    }

    private fun showMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun restore(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            val text = savedInstanceState.getString(EXTRA_TEXT)
            if (text != null) {
                editText.setText(text)
            }
            val deckId = savedInstanceState.getLong(EXTRA_DECK_ID)
            deckSelector.selectDeckWithId(deckId)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_TEXT, editText.text.toString())
        outState.putLong(EXTRA_DECK_ID, deckSelector.selectedDeck().id)
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, EnterFilePathActivity::class.java)
        }
    }

    @SuppressLint("SetTextI18n", "SdCardPath")
    private fun maybeSetDebugValues() {
        if (DEBUG_PREFILL_PATH) {
            editText.setText("/mnt/sdcard/import_from_file")
        }
    }
}