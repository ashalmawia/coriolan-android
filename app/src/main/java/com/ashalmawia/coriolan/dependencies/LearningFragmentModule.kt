package com.ashalmawia.coriolan.dependencies

import android.app.Dialog
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.ui.main.decks_list.BeginStudyListener
import com.ashalmawia.coriolan.ui.main.decks_list.DataFetcher
import com.ashalmawia.coriolan.ui.main.decks_list.DeckDetailsDialog
import com.ashalmawia.coriolan.ui.main.decks_list.DeckListItem
import com.ashalmawia.coriolan.ui.main.decks_list.DecksListAdapter
import com.ashalmawia.coriolan.ui.main.decks_list.IncreaseLimitsDialog
import org.joda.time.DateTime
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val DIALOG_DECK_DETAILS = "deck_details"
private const val DIALOG_INCREASE_LIMITS = "increase_limits"

val decksListFragmentModule = module {

    factory { (exercise: Exercise, dataFetcher: DataFetcher, beginStudyListener: BeginStudyListener) ->
        DecksListAdapter(
                get(),
                get(),
                exercise,
                dataFetcher,
                beginStudyListener,
                { deck: DeckListItem, date: DateTime ->
                    get(named(DIALOG_DECK_DETAILS)) { parametersOf(deck, date) } },
                { deck: DeckListItem, date: DateTime ->
                    get(named(DIALOG_INCREASE_LIMITS)) { parametersOf(deck, date) } }
        )
    }

    factory<Dialog>(named(DIALOG_DECK_DETAILS)) { (deck: DeckListItem, date: DateTime) ->
        DeckDetailsDialog(domainActivityScope().get(), deck, date, get())
    }

    factory<Dialog>(named(DIALOG_INCREASE_LIMITS)) { (deck: DeckListItem, date: DateTime) ->
        IncreaseLimitsDialog(domainActivityScope().get(), deck, date, get(), get(), get()).build()
    }

}