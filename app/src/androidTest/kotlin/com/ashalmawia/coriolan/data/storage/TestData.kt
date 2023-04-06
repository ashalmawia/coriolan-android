package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.backup.*
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.model.Extras

fun fillDatabase(count: Int, backupableRepository: BackupableRepository) {
    val languages = listOf(
            LanguageInfo(1L, "English"),
            LanguageInfo(2L, "Russian"),
            LanguageInfo(3L, "French"),
            LanguageInfo(4L, "Greek"),
            LanguageInfo(5L, "Chineese"),
            LanguageInfo(6L, "Polish"),
            LanguageInfo(7L, "Finnish"),
            LanguageInfo(8L, "Arabic")
    )
    val domains = listOf(
            DomainInfo(1L, "English", 1L, 2L),
            DomainInfo(2L, "French", 3L, 2L),
            DomainInfo(3L, "Greek", 4L, 2L),
            DomainInfo(4L, "Chineese", 5L, 2L),
            DomainInfo(5L, "Polish", 6L, 2L),
            DomainInfo(6L, "Finnish", 7L, 2L)
    )

    val decks = listOf(
            DeckInfo(1L, 1L, "Basic English"),
            DeckInfo(2L, 1L, "Advanced"),
            DeckInfo(3L, 2L, "Default"),
            DeckInfo(4L, 1L, "Some deck"),
            DeckInfo(5L, 1L, "Advanced deck"),
            DeckInfo(6L, 2L, "Another deck"),
            DeckInfo(7L, 1L, "Topic - Travelling"),
            DeckInfo(8L, 1L, "Topic - Music"),
            DeckInfo(9L, 2L, "Topic - Sports")
    )

    val terms = mutableListOf<TermInfo>()
    fun id() = terms.size.toLong()
    for (i in 1..count / 3 + 3) {
        terms.add(TermInfo(
                id(), "term with id: ${id()}", 1L, Extras("transcription with id: ${id()}")
        ))
        terms.add(TermInfo(
                id(), "term with id: ${id()}", 2L, Extras.empty()
        ))
        terms.add(TermInfo(
                id(), "term with id: ${id()}", 3L, Extras.empty()
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
        val termsOriginal = termsByLang[originalLangId]!!
        val termsTranslation = termsByLang[translationId]!!
        cards.add(CardInfo(
                id = i.toLong(),
                deckId = deckId,
                domainId = domainId,
                originalId = termsOriginal[i / 3].id,
                translationIds = (0 until 3).map { index -> termsTranslation[i / 3 + index].id }
        ))
    }

    val states = mutableListOf<ExerciseStateInfo>()
    for (i in 0 until count) {
        states.add(ExerciseStateInfo(
                (i + 1).toLong(), ExerciseId.FLASHCARDS, mockToday(), 4
        ))
        states.add(ExerciseStateInfo(
                (i + 1).toLong(), ExerciseId.TEST, mockToday().minus(5), -1
        ))
    }

    backupableRepository.overrideRepositoryData {
        it.writeLanguages(languages)
        it.writeDomains(domains)
        it.writeDecks(decks)
        it.writeTerms(terms)
        it.writeCards(cards)
        it.writeCardStates(states)
    }
}