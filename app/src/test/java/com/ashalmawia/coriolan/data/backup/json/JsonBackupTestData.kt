package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.*
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.model.Extras
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
            DomainInfo(1L, "English", 1L, 2L),
            DomainInfo(2L, "French", 3L, 2L),
            DomainInfo(3L, "Greek", 4L, 2L),
            DomainInfo(4L, "Chineese", 5L, 2L),
            DomainInfo(5L, "Polish", 6L, 2L),
            DomainInfo(6L, "Finnish", 7L, 2L)
    )

    val terms = listOf(
            // English
            TermInfo(1L, "shrimp", 1L, Extras.empty()),
            TermInfo(2L, "rocket", 1L, Extras("/ˈrɒkɪt \$ ˈrɑː-/")),
            TermInfo(3L, "spring", 1L, Extras("/sprɪŋ/")),
            TermInfo(4L, "summer", 1L, Extras("/ˈsʌmə \$ -ər/")),
            TermInfo(5L, "victory", 1L, Extras("/ˈvɪktəri/")),
            TermInfo(6L, "march", 1L, Extras("/mɑːtʃ \$ mɑːrtʃ/")),

            // Russian
            TermInfo(7L, "креветка", 2L, Extras.empty()),
            TermInfo(8L, "ракета", 2L, Extras.empty()),
            TermInfo(9L, "источник", 2L, Extras.empty()),
            TermInfo(10L, "весна", 2L, Extras.empty()),
            TermInfo(11L, "пружина", 2L, Extras.empty()),
            TermInfo(12L, "лето", 2L, Extras.empty()),
            TermInfo(13L, "победа", 2L, Extras.empty()),
            TermInfo(14L, "март", 2L, Extras.empty()),
            TermInfo(15L, "марш", 2L, Extras.empty()),

            // French
            TermInfo(16L, "ameloirer", 3L, Extras.empty()),
            TermInfo(17L, "chercher", 3L, Extras.empty()),
            TermInfo(18L, "voisin", 3L, Extras.empty()),

            // Russian
            TermInfo(19L, "улучшать", 2L, Extras.empty()),
            TermInfo(20L, "искать", 2L, Extras.empty()),
            TermInfo(21L, "сосед", 2L, Extras.empty())
    )

    val cards = listOf(
            CardInfo(1L, 1L, 1L, 1L, listOf(7L)),
            CardInfo(2L, 1L, 1L, 2L, listOf(8L)),
            CardInfo(3L, 2L, 1L, 3L, listOf(9L, 10L, 11L)),
            CardInfo(4L, 1L, 1L, 4L, listOf(12L)),
            CardInfo(5L, 2L, 1L, 5L, listOf(13L)),
            CardInfo(6L, 1L, 1L, 6L, listOf(14L, 15L)),

            CardInfo(7L, 1L, 1L, 7L, listOf(1L)),
            CardInfo(8L, 1L, 1L, 8L, listOf(2L)),
            CardInfo(9L, 2L, 1L, 9L, listOf(3L)),
            CardInfo(10L, 2L, 1L, 10L, listOf(3L)),
            CardInfo(11L, 2L, 1L, 11L, listOf(3L)),
            CardInfo(12L, 1L, 1L, 12L, listOf(4L)),
            CardInfo(13L, 2L, 1L, 13L, listOf(5L)),
            CardInfo(14L, 1L, 1L, 14L, listOf(6L)),
            CardInfo(15L, 1L, 1L, 15L, listOf(6L)),

            CardInfo(16L, 3L, 2L, 16L, listOf(19L)),
            CardInfo(17L, 3L, 2L, 17L, listOf(20L)),
            CardInfo(18L, 3L, 2L, 18L, listOf(21L)),

            CardInfo(19L, 3L, 2L, 19L, listOf(16L)),
            CardInfo(20L, 3L, 2L, 20L, listOf(17L)),
            CardInfo(21L, 3L, 2L, 21L, listOf(18L))
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

    private val random = Random()
    private val today = mockToday()
    private val exerciseId = ExerciseId.TEST

    val cardStates = listOf(
            ExerciseStateInfo(20L, exerciseId, today.minusDays(random.nextInt(500)), random.nextInt(500)),
            ExerciseStateInfo(6L, exerciseId, today.minusDays(random.nextInt(500)), random.nextInt(500)),
            ExerciseStateInfo(11L, exerciseId, today.plusDays(random.nextInt(500)), random.nextInt(500)),
            ExerciseStateInfo(1L, exerciseId, today.minusDays(random.nextInt(500)), random.nextInt(500)),
            ExerciseStateInfo(9L, exerciseId, today.minusDays(random.nextInt(500)), random.nextInt(500)),
            ExerciseStateInfo(10L, exerciseId, today.minusDays(random.nextInt(500)), random.nextInt(500)),
            ExerciseStateInfo(15L, exerciseId, today.plusDays(random.nextInt(500)), random.nextInt(500)),
            ExerciseStateInfo(21L, exerciseId, today, random.nextInt(500)),
            ExerciseStateInfo(2L, exerciseId, today.minusDays(random.nextInt(500)), random.nextInt(500)),
            ExerciseStateInfo(7L, exerciseId, today, random.nextInt(500))
    )
}