package com.ashalmawia.coriolan.data.backup

import android.os.Environment
import com.ashalmawia.coriolan.data.backup.json.JsonBackup
import com.ashalmawia.coriolan.learning.ExerciseDescriptor
import java.io.File
import java.io.InputStream
import java.io.OutputStream

interface Backup {

    companion object {
        private val rootDir = File(Environment.getExternalStorageDirectory(), "Coriolan")
        val backupDir = File(rootDir, "backup")

        fun get(): Backup = JsonBackup()
    }

    fun create(repository: BackupableRepository, exercises: List<ExerciseDescriptor<*, *>>, stream: OutputStream)

    fun restoreFrom(stream: InputStream, repository: BackupableRepository)
}