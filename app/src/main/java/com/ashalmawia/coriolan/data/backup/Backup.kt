package com.ashalmawia.coriolan.data.backup

import com.ashalmawia.coriolan.learning.exercise.Exercise
import java.io.InputStream
import java.io.OutputStream

interface Backup {

    fun create(repository: BackupableRepository, exercises: List<Exercise<*, *>>, stream: OutputStream)

    fun restoreFrom(stream: InputStream, repository: BackupableRepository)
}