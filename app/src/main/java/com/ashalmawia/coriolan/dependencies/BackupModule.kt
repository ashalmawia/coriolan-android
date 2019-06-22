package com.ashalmawia.coriolan.dependencies

import android.os.Environment
import com.ashalmawia.coriolan.data.backup.Backup
import com.ashalmawia.coriolan.data.backup.json.JsonBackup
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

const val BACKUP_DIR = "backup_dir"

val backupModule = module {

    single<Backup> { JsonBackup() }

    factory(named(BACKUP_DIR)) {
        createBackupDir()
    }

}

private fun createBackupDir(): File {
    val rootDir = File(Environment.getExternalStorageDirectory(), "Coriolan")
    return File(rootDir, "backup")
}