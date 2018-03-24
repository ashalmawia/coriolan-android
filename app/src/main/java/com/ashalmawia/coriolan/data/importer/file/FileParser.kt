package com.ashalmawia.coriolan.data.importer.file

import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.ExpressionType
import java.io.File
import java.util.regex.Pattern

class FileParser(val deck: Deck) {

    fun parseFile(file: File): List<CardData> {
        val lines = file.readLines()
        return parse(lines)
    }

    private fun parse(data: List<String>): List<CardData> {
        return data.mapNotNull { parseLine(it) }
    }

    /**
     * Matches something like:
     *  {original} {translation}
     * or
     *  {original} {translation1|translation2|translation3}
     */
    private val regexp = Pattern.compile("^[^{}]*\\{([^{}]*)\\}[^{}]*\\{([^{}]*)\\}[^{}]*$")
    fun parseLine(line: String): CardData? {
        val matcher = regexp.matcher(line)
        if (matcher.matches()) {
            val translationsData = matcher.group(2)
            return CardData(
                    matcher.group(1),
                    extractTranslations(translationsData),
                    deck.id,
                    ExpressionType.WORD
            )
        }

        if (line.isBlank()) {
            return null
        } else {
            throw ParsingException(line)
        }
    }

    private fun extractTranslations(data: String): List<String> {
        return data.split('|')
    }
}

class ParsingException(val line: String) : Exception("failed to parse the line[$line]")