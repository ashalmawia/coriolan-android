package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.*
import com.ashalmawia.coriolan.learning.mockToday
import java.util.*

object JsonBackupTestData {
    val languages = listOf(
            LanguageInfo(1L, "English"),
            LanguageInfo(2L, "Russian"),
            LanguageInfo(3L, "French")
    )
    val domains = listOf(
            DomainInfo(1L, "English", 1L, 2L),
            DomainInfo(2L, "French", 3L, 2L)
    )

    val exressions = listOf(
            // English
            ExpressionInfo(1L, "shrimp", 1L),
            ExpressionInfo(2L, "rocket", 1L),
            ExpressionInfo(3L, "spring", 1L),
            ExpressionInfo(4L, "summer", 1L),
            ExpressionInfo(5L, "victory", 1L),
            ExpressionInfo(6L, "march", 1L),

            // Russian
            ExpressionInfo(7L, "креветка", 2L),
            ExpressionInfo(8L, "ракета", 2L),
            ExpressionInfo(9L, "источник", 2L),
            ExpressionInfo(10L, "весна", 2L),
            ExpressionInfo(11L, "пружина", 2L),
            ExpressionInfo(12L, "лето", 2L),
            ExpressionInfo(13L, "победа", 2L),
            ExpressionInfo(14L, "март", 2L),
            ExpressionInfo(15L, "марш", 2L),

            // French
            ExpressionInfo(16L, "ameloirer", 3L),
            ExpressionInfo(17L, "chercher", 3L),
            ExpressionInfo(18L, "voisin", 3L),

            // Russian
            ExpressionInfo(19L, "улучшать", 2L),
            ExpressionInfo(20L, "искать", 2L),
            ExpressionInfo(21L, "сосед", 2L)
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
            DeckInfo(3L, 2L, "Default")
    )

    private val random = Random()
    private val today = mockToday()

    val srstates = listOf(
            SRStateInfo(20L, today.minusDays(random.nextInt(500)), random.nextInt(500)),
            SRStateInfo(6L, today.minusDays(random.nextInt(500)), random.nextInt(500)),
            SRStateInfo(11L, today.plusDays(random.nextInt(500)), random.nextInt(500)),
            SRStateInfo(1L, today.minusDays(random.nextInt(500)), random.nextInt(500)),
            SRStateInfo(9L, today.minusDays(random.nextInt(500)), random.nextInt(500)),
            SRStateInfo(10L, today.minusDays(random.nextInt(500)), random.nextInt(500)),
            SRStateInfo(15L, today.plusDays(random.nextInt(500)), random.nextInt(500)),
            SRStateInfo(21L, today, random.nextInt(500)),
            SRStateInfo(2L, today.minusDays(random.nextInt(500)), random.nextInt(500)),
            SRStateInfo(7L, today, random.nextInt(500))
    )
}