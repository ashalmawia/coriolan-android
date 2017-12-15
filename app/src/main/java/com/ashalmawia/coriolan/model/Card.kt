package com.ashalmawia.coriolan.model

import java.util.*

class Card private constructor(
        val original: Expression,
        val translations: List<Expression>,
        val state: State
) {

    companion object {
        fun create(original: Expression, translation: Expression) :Card {
            return Card(original, Arrays.asList(translation), stateCreateNew())
        }
    }

}

internal class State(
        private val added: Date,
        private var approvedLast: Date?,
        private var delay: Int?
) {
    fun status() :Status {
        if (delay == null) {
            return Status.NEW
        }
        if (delay!! > 50) {
            return Status.LEARNT
        } else {
            return Status.IN_PROGRESS
        }
    }
}

enum class Status {
    NEW, IN_PROGRESS, LEARNT
}

internal fun stateCreateNew() :State {
    return State(
            Date(),
            null,
            null
    )
}