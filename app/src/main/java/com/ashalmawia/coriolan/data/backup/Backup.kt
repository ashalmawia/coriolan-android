package com.ashalmawia.coriolan.data.backup

import com.ashalmawia.coriolan.data.backup.json.JsonBackup
import com.ashalmawia.coriolan.learning.ExerciseDescriptor
import java.io.InputStream
import java.io.OutputStream

interface Backup {

    companion object {
        fun get(): Backup = JsonBackup()
    }

    fun create(repository: BackupableRepository, exercises: List<ExerciseDescriptor<*, *>>, stream: OutputStream)

    fun restoreFrom(stream: InputStream, repository: BackupableRepository)
}