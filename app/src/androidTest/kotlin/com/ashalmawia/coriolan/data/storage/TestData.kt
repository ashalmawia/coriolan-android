package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.backup.*
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.util.asCardId
import com.ashalmawia.coriolan.util.asDeckId
import com.ashalmawia.coriolan.util.asDomainId
import com.ashalmawia.coriolan.util.asLanguageId

fun fillDatabase(count: Int, backupableRepository: BackupableRepository) {
    val languages = listOf(
            languageInfo(1L, "English"),
            languageInfo(2L, "Russian"),
            languageInfo(3L, "French"),
            languageInfo(4L, "Greek"),
            languageInfo(5L, "Chineese"),
            languageInfo(6L, "Polish"),
            languageInfo(7L, "Finnish"),
            languageInfo(8L, "Arabic")
    )
    val domains = listOf(
            domainInfo(1L, "English", 1L, 2L),
            domainInfo(2L, "French", 3L, 2L),
            domainInfo(3L, "Greek", 4L, 2L),
            domainInfo(4L, "Chineese", 5L, 2L),
            domainInfo(5L, "Polish", 6L, 2L),
            domainInfo(6L, "Finnish", 7L, 2L)
    )

    val decks = listOf(
            deckInfo(1L, 1L, "Basic English"),
            deckInfo(2L, 1L, "Advanced"),
            deckInfo(3L, 2L, "Default"),
            deckInfo(4L, 1L, "Some deck"),
            deckInfo(5L, 1L, "Advanced deck"),
            deckInfo(6L, 2L, "Another deck"),
            deckInfo(7L, 1L, "Topic - Travelling"),
            deckInfo(8L, 1L, "Topic - Music"),
            deckInfo(9L, 2L, "Topic - Sports")
    )

    val terms = mutableListOf<TermInfo>()
    fun id() = terms.size.toLong() + 1L
    for (i in 1..count / 3 + 10) {
        terms.add(termInfo(
                id(), "term with id: ${id()}", 1L, "transcription with id: ${id()}"
        ))
        terms.add(termInfo(
                id(), "term with id: ${id()}", 2L, null
        ))
        terms.add(termInfo(
                id(), "term with id: ${id()}", 3L, null
        ))
    }

    val termsByLang = terms.groupBy { it.languageId }

    val cards = mutableListOf<CardInfo>()
    for (i in 1..count) {
        val domainId = (i % 2 + 1).toLong()
        val deckId = if (domainId == 1L) {
            if (i % 3 == 0) 1L else 2L
        } else 3L
        val originalLangId = if (domainId == 1L) 1L else 3L
        val translationId = 2L
        val termsOriginal = termsByLang[originalLangId.asLanguageId()]!!
        val termsTranslation = termsByLang[translationId.asLanguageId()]!!
        cards.add(CardInfo(
                id = i.toLong().asCardId(),
                deckId = deckId.asDeckId(),
                domainId = domainId.asDomainId(),
                originalId = termsOriginal[i / 3].id,
                translationIds = (0 until 3).map { index -> termsTranslation[i / 3 + index].id },
                CardType.FORWARD
        ))
    }

    val states = mutableListOf<LearningProgressInfo>()
    for (i in 0 until count) {
        states.add(learningProgressInfo(
                (i + 1).toLong(), mockToday(), 4
        ))
        states.add(learningProgressInfo(
                (i + 1).toLong(), mockToday().minus(5), -1
        ))
    }

    backupableRepository.apply {
        beginTransaction()
        writeLanguages(languages)
        writeDomains(domains)
        writeDecks(decks)
        writeTerms(terms)
        writeCards(cards)
        writeExerciseStates(states)
        setTransactionSuccessful()
        endTransaction()
    }
}