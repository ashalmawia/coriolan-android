package com.ashalmawia.coriolan.model

class Card private constructor(
        val id: Long,
        val original: Expression,
        val translations: List<Expression>
) {

    companion object {
        fun create(id: Long, original: Expression, translations: List<Expression>): Card {
            return Card(id, original, translations)
        }
    }

}