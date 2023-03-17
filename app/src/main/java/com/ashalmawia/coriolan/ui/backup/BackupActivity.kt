package com.ashalmawia.coriolan.ui.backup

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.backup.Backup
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.view.visible
import kotlinx.android.synthetic.main.backup.*
import org.joda.time.DateTime
import org.koin.android.ext.android.inject
import java.io.File

class BackupActivity : BaseActivity(), BackupCreationListener {

    private val backupDir by lazy { BackupUtils.createBackupDir(this) }
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
        createBackup()
    }

    private fun updateUiCreatingBackup() {
        buttonOk.isEnabled = false
        buttonCancel.isEnabled = false

        labelCreating.visible = true
        dividerCreating.visible = true
    }

    private fun createBackup() {
        updateUiCreatingBackup()

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
        labelPath.text = file.absolutePath
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