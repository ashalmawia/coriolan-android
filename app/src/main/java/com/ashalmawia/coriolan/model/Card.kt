package com.ashalmawia.coriolan.model

import java.util.*

class Card private constructor(
        val id: Int,
        val original: Expression,
        val translations: List<Expression>) {

    companion object {
        fun create(id: Int, original: Expression, translation: Expression) :Card {
            return Card(id, original, Arrays.asList(translation))
        }
    }

}