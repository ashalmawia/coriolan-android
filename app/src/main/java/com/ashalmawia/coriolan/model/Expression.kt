package com.ashalmawia.coriolan.model

data class Expression(
        val id: Long,
        val value: String,
        val type: ExpressionType,
        val language: Language
)

enum class ExpressionType(val value: Int) {
    UNKNOWN(-1), WORD(0)
}

fun toExpressionType(value: Int): ExpressionType {
    return ExpressionType.values().find { it.value == value } ?: ExpressionType.UNKNOWN
}