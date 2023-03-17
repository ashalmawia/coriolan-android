package com.ashalmawia.coriolan.ui.backup

import android.content.Context
import java.io.File

object BackupUtils {

    fun createBackupDir(context: Context): File {
        val rootDir = context.getExternalFilesDir(null)
        return File(rootDir, "backup")
    }
}