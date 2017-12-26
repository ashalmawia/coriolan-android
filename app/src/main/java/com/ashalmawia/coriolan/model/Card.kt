package com.ashalmawia.coriolan.model

import java.util.*

class Card private constructor(
        val id: Long,
        val original: Expression,
        val translations: List<Expression>  //TODO: currently only single translation is supported: https://trello.com/c/EJBtdetZ
) {

    companion object {
        fun create(id: Long, original: Expression, translation: Expression): Card {
            return Card(id, original, Arrays.asList(translation))
        }
    }

}