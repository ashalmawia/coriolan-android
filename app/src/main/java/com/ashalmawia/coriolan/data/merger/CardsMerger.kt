package com.ashalmawia.coriolan.data.merger

import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.Term

interface CardsMerger {

    companion object {
        fun create(repository: Repository, domain: Domain, exercisesRegistry: ExercisesRegistry): CardsMerger {
            return CardsMergerImpl(repository, domain, exercisesRegistry)
        }
    }

    fun mergeOrAdd(original: Term, translations: List<Term>, deckId: Long)
}