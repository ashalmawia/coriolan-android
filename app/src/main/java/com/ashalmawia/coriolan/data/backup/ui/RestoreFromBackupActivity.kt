package com.ashalmawia.coriolan.data.backup.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.annotation.StringRes
import android.widget.Toast
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.backup.Backup
import com.ashalmawia.coriolan.data.backup.Backup.Companion.backupDir
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.view.visible
import com.ashalmawia.coriolan.util.restartApp
import kotlinx.android.synthetic.main.restore_from_backup.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import java.io.File

@RuntimePermissions
class RestoreFromBackupActivity : BaseActivity(), BackupRestoringListener {

    private var task: RestoreFromBackupAsyncTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restore_from_backup)

        setUpToolbar(R.string.backup__restore_title, false)

        buttonOk.setOnClickListener { onSelectAndRestoreClicked() }
        buttonCancel.setOnClickListener { finish() }
    }

    private fun onSelectAndRestoreClicked() {
        selectBackupWithPermissionCheck()
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun selectBackup() {
        if (!backupDir.exists()) {
            onError(R.string.backup__restore_failed_no_backup)
            return
        }

        val files = backupDir.listFiles { file -> file.isFile && file.name.endsWith(".coriolan") }
        if (files.isEmpty()) {
            onError(R.string.backup__restore_failed_no_backup)
            return
        }

        val fileNames = files.map { it.name }.toTypedArray()

        val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.backup__restore_select_file)
                .setSingleChoiceItems(fileNames, 0, { dialog, position ->
                    onFileSelected(files[position])
                    dialog.dismiss()
                })
                .create()
        dialog.show()
    }

    private fun onFileSelected(file: File) {
        val repo = BackupableRepository.get(this)
        val hasValuableData = repo.hasAtLeastOneCard()

        if (hasValuableData) {
            val dialog = AlertDialog.Builder(this, R.style.Coriolan_Theme_Dialog)
                    .setTitle(R.string.backup__restore_final_warning_title)
                    .setMessage(R.string.backup__restore_final_warning_message)
                    .setNegativeButton(R.string.button_cancel, null)
                    .setPositiveButton(R.string.backup__restore_confirm, { _, _ -> onRestoreConfirmed(file, repo) })
            dialog.show()
        } else {
            onRestoreConfirmed(file, repo)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onRestoreConfirmed(file: File, repo: BackupableRepository) {
        buttonOk.isEnabled = false
        buttonCancel.isEnabled = false

        dividerRestoring.visible = true
        labelStatus.visible = true

        labelPath.text = "sdcard/Coriolan/backup/${file.name}"
        labelPath.visible = true

        labelRestoring.visible = true
        progress.visible = true

        restoreFrom(file, repo)
    }

    private fun restoreFrom(file: File, repo: BackupableRepository) {
        val task = RestoreFromBackupAsyncTask(repo, file, Preferences.get(this))
        task.listener = this
        this.task = task

        task.execute()
    }

    override fun onRestored() {
        val repository = Repository.get(this)
        repository.invalidateCache()

        labelStatus.setText(R.string.backup__restore_success)
        labelRestoring.visible = false

        progress.visible = false

        buttonCancel.visible = false
        buttonOk.isEnabled = true
        buttonOk.setText(R.string.button_ok)
        buttonOk.setOnClickListener { restartApp() }
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onPermissionDenied() {
        finish()
    }

    override fun onError(@StringRes messageRes: Int) {
        Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
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
        private val repo: BackupableRepository,
        private val backupFile: File,
        private val preferences: Preferences
) : AsyncTask<Any, Unit, Boolean>() {

    var listener: BackupRestoringListener? = null

    override fun doInBackground(vararg params: Any): Boolean {
        if (!backupFile.exists() || !backupFile.isFile) {
            return false
        }

        val backup = Backup.get()

        backupFile.inputStream().use {
            backup.restoreFrom(it, repo)
            preferences.clearLastTranslationsLanguageId()
        }

        return true
    }

    override fun onPostExecute(success: Boolean) {
        val listener = this.listener
        if (listener != null) {
            if (success) {
                listener.onRestored()
            } else {
                listener.onError(R.string.backup__restore_failed)
            }
        }
    }
}

private interface BackupRestoringListener {

    fun onRestored()

    fun onError(@StringRes messageRes: Int)
}