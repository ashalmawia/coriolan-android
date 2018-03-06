package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import org.joda.time.DateTime

object MutationRegistry {

    fun mutations(preferences: Preferences, journal: Journal, date: DateTime, random: Boolean): Mutations {
        return Mutations(listOf(
                ShuffleMutation(random),
                LimitCountMutation(preferences, journal, date)
        ))
    }
}