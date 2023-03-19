package com.ashalmawia.coriolan.data.backup

import java.io.InputStream
import java.io.OutputStream

interface Backup {

    fun create(repository: BackupableRepository, stream: OutputStream)

    fun restoreFrom(stream: InputStream, repository: BackupableRepository)
}