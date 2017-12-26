package com.ashalmawia.coriolan.data.importer.file

import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.model.ExpressionType
import java.io.File
import java.util.regex.Pattern

class FileParser(private val decksRegistry: DecksRegistry) {

    fun parseFile(file: File): List<CardData> {
        val lines = file.readLines()
        return parse(lines)
    }

    private fun parse(data: List<String>): List<CardData> {
        return data.mapNotNull { parseLine(it) }
    }

    private val regexp = Pattern.compile("^.*\\{(.*)\\}.*\\{(.*)\\}.*$")
    fun parseLine(line: String): CardData? {
        val matcher = regexp.matcher(line)
        if (matcher.matches()) {
            return CardData(matcher.group(1), matcher.group(2), decksRegistry.default().id, ExpressionType.WORD)
        }

        if (line.isBlank()) {
            return null
        } else {
            throw ParsingException(line)
        }
    }
}

class ParsingException(val line: String) : Exception("failed to parse the line[$line]")