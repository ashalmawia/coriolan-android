package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import org.joda.time.DateTime

object MutationRegistry {

    fun mutations(preferences: Preferences, journal: Journal, date: DateTime, random: Boolean): Mutations {
        return Mutations(listOf(
                // order matters
                ShuffleMutation(random),
                CardTypeFilterMutation.from(preferences),
                LimitCountMutation(preferences, journal, date)
        ))
    }
}