package com.ashalmawia.coriolan.data.backup

interface BackupableEntity {

    fun beginTransaction()

    fun endTransaction()

    fun setTransactionSuccessful()

    fun dropAllData()

}