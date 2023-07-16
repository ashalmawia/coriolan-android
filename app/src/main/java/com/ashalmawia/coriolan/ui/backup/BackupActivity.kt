package com.ashalmawia.coriolan.ui.backup

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.backup.Backup
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.logbook.BackupableLogbook
import com.ashalmawia.coriolan.databinding.BackupBinding
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.view.visible
import org.joda.time.DateTime
import org.koin.android.ext.android.inject

class BackupActivity : BaseActivity(), BackupCreationListener {
    
    private val views by lazy { BackupBinding.inflate(layoutInflater) }

    private val backupableRepository: BackupableRepository by inject()
    private val backupableLogbook: BackupableLogbook by inject()
    private val backup: Backup by inject()

    private val createDocumentLauncher = registerForActivityResult(
            ActivityResultContracts.CreateDocument("*/*")) { uri ->
        createBackup(uri)
    }

    private var task: BackupAsyncTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)

        setUpToolbar(titleRes = R.string.backup__create_title, cancellable = false)

        views.buttonOk.setOnClickListener { onCreateBackupClicked() }
        views.buttonCancel.setOnClickListener { finish() }
    }

    private fun onCreateBackupClicked() {
        createDocumentLauncher.launch(fileName())
    }

    private fun updateUiCreatingBackup() {
        views.apply {
            buttonOk.isEnabled = false
            buttonCancel.isEnabled = false

            labelCreating.visible = true
            dividerCreating.root.visible = true
        }
    }

    private fun createBackup(uri: Uri?) {
        uri ?: return

        updateUiCreatingBackup()

        val resolver = applicationContext.contentResolver
        val task = BackupAsyncTask(resolver, backupableRepository, backupableLogbook, uri, backup)
        task.listener = this
        this.task = task

        task.execute()
    }

    @SuppressLint("SetTextI18n")
    override fun onBackupCreated(uri: Uri) {
        views.apply {
            labelCreating.visible = false
            dividerCreating.root.visible = false

            labelCreated.setText(R.string.backup__created)
            labelCreated.visible = true
            labelPath.text = uri.path
            labelPath.visible = true
            dividerCreated.root.visible = true

            buttonCancel.visible = false
            buttonOk.isEnabled = true
            buttonOk.setText(R.string.button_ok)
            buttonOk.setOnClickListener { finish() }
        }
    }

    override fun onError() {
        Toast.makeText(this, R.string.backup__creation_failed, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onStop() {
        super.onStop()
        task?.listener = null
        task = null
    }

    private fun fileName(): String {
        val time = DateTime.now().toString("yyyy-MM-dd_HH:mm:ss")
        return "coriolan_$time.backup"
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, BackupActivity::class.java)
        }
    }
}

private class BackupAsyncTask(
        private val resolver: ContentResolver,
        private val repo: BackupableRepository,
        private val logbook: BackupableLogbook,
        private val backupUri: Uri,
        private val backup: Backup
) : AsyncTask<Any, Nothing, Uri?>() {

    var listener: BackupCreationListener? = null

    override fun doInBackground(vararg params: Any): Uri? {
        val stream = resolver.openOutputStream(backupUri) ?: return null

        stream.use {
            backup.create(repo, logbook, it)
        }
        return backupUri
    }

    override fun onPostExecute(uri: Uri?) {
        if (uri != null) {
            listener?.onBackupCreated(uri)
        } else {
            listener?.onError()
        }
    }
}

private interface BackupCreationListener {

    fun onBackupCreated(uri: Uri)

    fun onError()
}