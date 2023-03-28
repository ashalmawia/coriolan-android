package com.ashalmawia.coriolan.ui.backup

import android.os.Environment
import java.io.File

object BackupUtils {

    fun backupDirectory(): File {
        val rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        return File(rootDir, "Coriolan/backup")
    }
}