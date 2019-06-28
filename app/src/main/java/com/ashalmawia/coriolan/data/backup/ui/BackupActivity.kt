package com.ashalmawia.coriolan.data.backup.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.backup.Backup
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.learning.ExercisesRegistry
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.view.visible
import kotlinx.android.synthetic.main.backup.*
import org.joda.time.DateTime
import org.koin.android.ext.android.inject
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import java.io.File

@RuntimePermissions
class BackupActivity : BaseActivity(), BackupCreationListener {

    private val rootDir = File(Environment.getExternalStorageDirectory(), "Coriolan")
    private val backupDir = File(rootDir, "backup")

    private val backupableRepository: BackupableRepository by inject()
    private val backup: Backup by inject()
    private val exercisesRegistry: ExercisesRegistry by inject()

    private var task: BackupAsyncTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.backup)

        setUpToolbar(R.string.backup__create_title, false)

        buttonOk.setOnClickListener { onCreateBackupClicked() }
        buttonCancel.setOnClickListener { finish() }
    }

    private fun onCreateBackupClicked() {
        buttonOk.isEnabled = false
        buttonCancel.isEnabled = false

        labelCreating.visible = true
        dividerCreating.visible = true

        createBackupWithPermissionCheck()
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun createBackup() {
        val task = BackupAsyncTask(backupableRepository, backupDir, backup, exercisesRegistry)
        task.listener = this
        this.task = task

        task.execute()
    }

    @SuppressLint("SetTextI18n")
    override fun onBackupCreated(file: File) {
        labelCreating.visible = false
        dividerCreating.visible = false

        labelCreated.setText(R.string.backup__created)
        labelCreated.visible = true
        labelPath.text = "sdcard/Coriolan/backup/${file.name}"
        labelPath.visible = true
        dividerCreated.visible = true

        buttonCancel.visible = false
        buttonOk.isEnabled = true
        buttonOk.setText(R.string.button_ok)
        buttonOk.setOnClickListener { finish() }
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    override fun onError() {
        Toast.makeText(this, R.string.backup__creation_failed, Toast.LENGTH_SHORT).show()
        finish()
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
            return Intent(context, BackupActivity::class.java)
        }
    }
}

private class BackupAsyncTask(
        private val repo: BackupableRepository,
        private val backupDir: File,
        private val backup: Backup,
        private val exercisesRegistry: ExercisesRegistry
) : AsyncTask<Any, Nothing, File?>() {

    var listener: BackupCreationListener? = null

    override fun doInBackground(vararg params: Any): File? {
        if (!backupDir.exists()) {
            val result = backupDir.mkdirs()
            if (!result) {
                return null
            }
        }

        val file = File(backupDir, name())
        if (!file.createNewFile()) {
            return null
        }

        file.outputStream().use {
            backup.create(repo, exercisesRegistry.allExercises(), it)
        }

        return file
    }

    private fun name(): String {
        val time = DateTime.now().toString("yyyy-MM-dd_HH:mm:ss")
        return "backup_$time.coriolan"
    }

    override fun onPostExecute(file: File?) {
        val listener = this.listener
        if (listener != null) {
            if (file != null) {
                listener.onBackupCreated(file)
            } else {
                listener.onError()
            }
        }
    }
}

private interface BackupCreationListener {

    fun onBackupCreated(file: File)

    fun onError()
}