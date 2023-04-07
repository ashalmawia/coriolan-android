package com.ashalmawia.coriolan.data.storage.sqlite.contract

object SqliteUtils {

    fun allColumns(columns: Array<String>, alias: String?): String {
        if (alias == null) {
            return allColumnsWithoutAlias(columns)
        } else {
            return withAlias(columns, alias)
        }
    }

    private fun allColumnsWithoutAlias(columns: Array<String>): String
            = columns.joinToString { it }

    fun withAlias(columns: Array<String>, alias: String): String
            = columns.joinToString { """$alias.$it AS "${it.from(alias)}"""" }

    fun String.from(alias: String?): String = if (alias != null) "${alias}_$this" else this

}