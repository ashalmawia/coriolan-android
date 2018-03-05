package com.ashalmawia.coriolan.model

data class Domain(
        val id: Long,
        val name: String,
        val langOriginal: Language,
        val langTranslations: Language
)