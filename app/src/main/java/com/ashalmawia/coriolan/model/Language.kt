package com.ashalmawia.coriolan.model

import java.io.Serializable

data class Language(
        val id: LanguageId,
        val value: String
)

data class LanguageId(val value: Long) : Serializable {

    fun asString() = value.toString()
}