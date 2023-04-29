package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.learning.exercise.CardAction
import com.ashalmawia.coriolan.util.orZero
import org.joda.time.DateTime
import java.lang.Integer.max

interface StudyTargetsResolver {

    fun defaultStudyTargets(date: DateTime): StudyTargets
}

class StudyTargetsResolverImpl(private val preferences: Preferences, private val logbook: Logbook) : StudyTargetsResolver {

    override fun defaultStudyTargets(date: DateTime): StudyTargets {
        val limitNew = preferences.getNewCardsDailyLimit(date)
        val limitReview = preferences.getReviewCardsDailyLimit(date)
        val alreadyStudied = logbook.cardsStudiedOnDate(date)

        return StudyTargets(
                limitNew?.minus(alreadyStudied[CardAction.NEW_CARD_FIRST_SEEN].orZero())?.run { max(this, 0) },
                limitReview?.minus(alreadyStudied[CardAction.CARD_REVIEWED].orZero())?.run { max(this, 0) }
        )
    }
}