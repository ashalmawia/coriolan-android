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
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.view.visible
import kotlinx.android.synthetic.main.backup.*
import org.joda.time.DateTime
import org.koin.android.ext.android.inject

class BackupActivity : BaseActivity(), BackupCreationListener {

    private val backupableRepository: BackupableRepository by inject()
    private val backup: Backup by inject()

    private val createDocumentLauncher = registerForActivityResult(
            ActivityResultContracts.CreateDocument("*/*")) { uri ->
        createBackup(uri)
    }

    private var task: BackupAsyncTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.backup)

        setUpToolbar(R.string.backup__create_title, false)

        buttonOk.setOnClickListener { onCreateBackupClicked() }
        buttonCancel.setOnClickListener { finish() }
    }

    private fun onCreateBackupClicked() {
        createDocumentLauncher.launch(fileName())
    }

    private fun updateUiCreatingBackup() {
        buttonOk.isEnabled = false
        buttonCancel.isEnabled = false

        labelCreating.visible = true
        dividerCreating.visible = true
    }

    private fun createBackup(uri: Uri?) {
        uri ?: return

        updateUiCreatingBackup()

        val resolver = applicationContext.contentResolver
        val task = BackupAsyncTask(resolver, backupableRepository, uri, backup)
        task.listener = this
        this.task = task

        task.execute()
    }

    @SuppressLint("SetTextI18n")
    override fun onBackupCreated(uri: Uri) {
        labelCreating.visible = false
        dividerCreating.visible = false

        labelCreated.setText(R.string.backup__created)
        labelCreated.visible = true
        labelPath.text = uri.path
        labelPath.visible = true
        dividerCreated.visible = true

        buttonCancel.visible = false
        buttonOk.isEnabled = true
        buttonOk.setText(R.string.button_ok)
        buttonOk.setOnClickListener { finish() }
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
        private val backupUri: Uri,
        private val backup: Backup
) : AsyncTask<Any, Nothing, Uri?>() {

    var listener: BackupCreationListener? = null

    override fun doInBackground(vararg params: Any): Uri? {
        val stream = resolver.openOutputStream(backupUri) ?: return null

        stream.use {
            backup.create(repo, it)
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