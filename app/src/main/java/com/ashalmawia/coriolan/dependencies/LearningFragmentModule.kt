package com.ashalmawia.coriolan.dependencies

import android.app.Dialog
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.*
import org.joda.time.DateTime
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val DIALOG_DECK_DETAILS = "deck_details"
private const val DIALOG_INCREASE_LIMITS = "increase_limits"

val learningFragmentModule = module {

    factory { (exercise: Exercise<*, *>, dataFetcher: DataFetcher, beginStudyListener: BeginStudyListener) ->
        DecksAdapter(
                get(),
                exercise,
                dataFetcher,
                beginStudyListener,
                { deck: Deck, date: DateTime ->
                    get(named(DIALOG_DECK_DETAILS)) { parametersOf(deck, exercise, date) } },
                { deck: Deck, date: DateTime ->
                    get(named(DIALOG_INCREASE_LIMITS)) { parametersOf(deck, exercise, date) } }
        )
    }

    factory<Dialog>(named(DIALOG_DECK_DETAILS)) { (deck: Deck, exercise: Exercise<*, *>, date: DateTime) ->
        DeckDetailsDialog(domainActivityScope().get(), deck, exercise, date, get())
    }

    factory<Dialog>(named(DIALOG_INCREASE_LIMITS)) { (deck: Deck, exercise: Exercise<*, *>, date: DateTime) ->
        IncreaseLimitsDialog(domainActivityScope().get(), deck, exercise, date, get(), get()).build()
    }

}