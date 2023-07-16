package com.ashalmawia.coriolan.data.backup

import com.ashalmawia.coriolan.data.logbook.BackupableLogbook
import java.io.InputStream
import java.io.OutputStream

interface Backup {

    fun create(repository: BackupableRepository, logbook: BackupableLogbook, stream: OutputStream)

    fun restoreFrom(stream: InputStream, repository: BackupableRepository, logbook: BackupableLogbook)
}