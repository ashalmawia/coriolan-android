package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.*
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.model.CardType
import java.util.*

object JsonBackupTestData {

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
            domainInfo(1L, "English", 1L, 2L),
            domainInfo(2L, "French", 3L, 2L),
            domainInfo(3L, "Greek", 4L, 2L),
            domainInfo(4L, "Chineese", 5L, 2L),
            domainInfo(5L, "Polish", 6L, 2L),
            domainInfo(6L, "Finnish", 7L, 2L)
    )

    val terms = listOf(
            // English
            TermInfo(1L, "shrimp", 1L, null),
            TermInfo(2L, "rocket", 1L, "/ˈrɒkɪt \$ ˈrɑː-/"),
            TermInfo(3L, "spring", 1L, "/sprɪŋ/"),
            TermInfo(4L, "summer", 1L, "/ˈsʌmə \$ -ər/"),
            TermInfo(5L, "victory", 1L, "/ˈvɪktəri/"),
            TermInfo(6L, "march", 1L, "/mɑːtʃ \$ mɑːrtʃ/"),

            // Russian
            TermInfo(7L, "креветка", 2L, null),
            TermInfo(8L, "ракета", 2L, null),
            TermInfo(9L, "источник", 2L, null),
            TermInfo(10L, "весна", 2L, null),
            TermInfo(11L, "пружина", 2L, null),
            TermInfo(12L, "лето", 2L, null),
            TermInfo(13L, "победа", 2L, null),
            TermInfo(14L, "март", 2L, null),
            TermInfo(15L, "марш", 2L, null),

            // French
            TermInfo(16L, "ameloirer", 3L, null),
            TermInfo(17L, "chercher", 3L, null),
            TermInfo(18L, "voisin", 3L, null),

            // Russian
            TermInfo(19L, "улучшать", 2L, null),
            TermInfo(20L, "искать", 2L, null),
            TermInfo(21L, "сосед", 2L, null)
    )

    val cards = listOf(
            cardInfo(1L, 1L, 1L, 1L, listOf(7L), CardType.FORWARD),
            cardInfo(2L, 1L, 1L, 2L, listOf(8L), CardType.FORWARD),
            cardInfo(3L, 2L, 1L, 3L, listOf(9L, 10L, 11L), CardType.FORWARD),
            cardInfo(4L, 1L, 1L, 4L, listOf(12L), CardType.FORWARD),
            cardInfo(5L, 2L, 1L, 5L, listOf(13L), CardType.FORWARD),
            cardInfo(6L, 1L, 1L, 6L, listOf(14L, 15L), CardType.FORWARD),

            cardInfo(7L, 1L, 1L, 7L, listOf(1L), CardType.REVERSE),
            cardInfo(8L, 1L, 1L, 8L, listOf(2L), CardType.REVERSE),
            cardInfo(9L, 2L, 1L, 9L, listOf(3L), CardType.REVERSE),
            cardInfo(10L, 2L, 1L, 10L, listOf(3L), CardType.REVERSE),
            cardInfo(11L, 2L, 1L, 11L, listOf(3L), CardType.REVERSE),
            cardInfo(12L, 1L, 1L, 12L, listOf(4L), CardType.REVERSE),
            cardInfo(13L, 2L, 1L, 13L, listOf(5L), CardType.REVERSE),
            cardInfo(14L, 1L, 1L, 14L, listOf(6L), CardType.REVERSE),
            cardInfo(15L, 1L, 1L, 15L, listOf(6L), CardType.REVERSE),

            cardInfo(16L, 3L, 2L, 16L, listOf(19L), CardType.FORWARD),
            cardInfo(17L, 3L, 2L, 17L, listOf(20L), CardType.FORWARD),
            cardInfo(18L, 3L, 2L, 18L, listOf(21L), CardType.FORWARD),

            cardInfo(19L, 3L, 2L, 19L, listOf(16L), CardType.REVERSE),
            cardInfo(20L, 3L, 2L, 20L, listOf(17L), CardType.REVERSE),
            cardInfo(21L, 3L, 2L, 21L, listOf(18L), CardType.REVERSE)
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

    private val random = Random()
    private val today = mockToday()

    val cardStates = listOf(
            LearningProgressInfo(20L, today.minusDays(random.nextInt(500)), random.nextInt(500)),
            LearningProgressInfo(6L, today.minusDays(random.nextInt(500)), random.nextInt(500)),
            LearningProgressInfo(11L, today.plusDays(random.nextInt(500)), random.nextInt(500)),
            LearningProgressInfo(1L, today.minusDays(random.nextInt(500)), random.nextInt(500)),
            LearningProgressInfo(9L, today.minusDays(random.nextInt(500)), random.nextInt(500)),
            LearningProgressInfo(10L, today.minusDays(random.nextInt(500)), random.nextInt(500)),
            LearningProgressInfo(15L, today.plusDays(random.nextInt(500)), random.nextInt(500)),
            LearningProgressInfo(21L, today, random.nextInt(500)),
            LearningProgressInfo(2L, today.minusDays(random.nextInt(500)), random.nextInt(500)),
            LearningProgressInfo(7L, today, random.nextInt(500))
    )
}