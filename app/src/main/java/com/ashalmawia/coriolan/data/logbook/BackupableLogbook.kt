package com.ashalmawia.coriolan.data.logbook

import com.ashalmawia.coriolan.data.backup.BackupableEntity

interface BackupableLogbook : BackupableEntity {

    fun overrideAllData(data: List<LogbookEntryInfo>)

    fun exportAllData(offset: Int, limit: Int): List<LogbookEntryInfo>
}

data class LogbookEntryInfo(val date: Long, val payload: String)