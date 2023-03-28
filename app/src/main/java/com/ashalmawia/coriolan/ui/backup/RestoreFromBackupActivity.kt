package com.ashalmawia.coriolan.ui.backup

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.annotation.StringRes
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.backup.Backup
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.util.finishWithAlert
import com.ashalmawia.coriolan.ui.view.visible
import com.ashalmawia.coriolan.util.restartApp
import kotlinx.android.synthetic.main.restore_from_backup.*
import org.koin.android.ext.android.inject

private const val TAG = "RestoreFromBackupActivity"

class RestoreFromBackupActivity : BaseActivity(), BackupRestoringListener {

    private var task: RestoreFromBackupAsyncTask? = null

    private val repository: Repository by inject()
    private val backupableRepository: BackupableRepository by inject()
    private val backup: Backup by inject()
    private val preferences: Preferences by inject()

    private val openDocumentLauncher = registerForActivityResult(OpenDocument(), this::onFileSelected)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restore_from_backup)

        setUpToolbar(R.string.backup__restore_title, false)

        buttonOk.setOnClickListener { selectBackup() }
        buttonCancel.setOnClickListener { finish() }
    }

    private fun selectBackup() {
        openDocumentLauncher.launch(arrayOf("*/*"))
    }

    private fun onFileSelected(uri: Uri?) {
        uri ?: return

        val hasValuableData = backupableRepository.hasAtLeastOneCard()

        if (hasValuableData) {
            val dialog = AlertDialog.Builder(this, R.style.Coriolan_Theme_Dialog)
                    .setTitle(R.string.backup__restore_final_warning_title)
                    .setMessage(R.string.backup__restore_final_warning_message)
                    .setNegativeButton(R.string.button_cancel, null)
                    .setPositiveButton(R.string.backup__restore_confirm) { _, _ -> onRestoreConfirmed(uri) }
            dialog.show()
        } else {
            onRestoreConfirmed(uri)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onRestoreConfirmed(uri: Uri) {
        buttonOk.visible = false
        buttonCancel.visible = false

        dividerRestoring.visible = true
        labelStatus.visible = true

        labelPath.text = uri.path
        labelPath.visible = true

        labelRestoring.visible = true
        progress.visible = true

        restoreFrom(uri)
    }

    private fun restoreFrom(uri: Uri) {
        val resolver = applicationContext.contentResolver
        val task = RestoreFromBackupAsyncTask(resolver, backupableRepository, uri, backup, preferences)
        task.listener = this
        this.task = task

        task.execute()
    }

    override fun onRestored() {
        repository.invalidateCache()

        labelStatus.setText(R.string.backup__restore_success)
        labelRestoring.visible = false

        progress.visible = false

        buttonCancel.visible = false
        buttonOk.visible = true
        buttonOk.setText(R.string.button_ok)
        buttonOk.setOnClickListener { restartApp() }
    }

    override fun onError(@StringRes messageRes: Int) {
        finishWithAlert(R.string.backup__restore_failed_title, messageRes)
    }

    override fun onStop() {
        super.onStop()
        task?.listener = null
        task = null
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, RestoreFromBackupActivity::class.java)
        }
    }
}

private class RestoreFromBackupAsyncTask(
        private val resolver: ContentResolver,
        private val repo: BackupableRepository,
        private val backupFile: Uri,
        private val backup: Backup,
        private val preferences: Preferences
) : AsyncTask<Any, Unit, Boolean>() {

    var listener: BackupRestoringListener? = null

    override fun doInBackground(vararg params: Any): Boolean {
        val stream = resolver.openInputStream(backupFile) ?: return false

        try {
            stream.use {
                backup.restoreFrom(it, repo)
                preferences.clearLastTranslationsLanguageId()
            }
            return true
        } catch (e: Throwable) {
            Log.e(TAG, "failed to restore from backup", e)
            return false
        }
    }

    override fun onPostExecute(success: Boolean) {
        if (success) {
            listener?.onRestored()
        } else {
            listener?.onError(R.string.backup__restore_failed)
        }
    }
}

private interface BackupRestoringListener {

    fun onRestored()

    fun onError(@StringRes messageRes: Int)
}