package com.ashalmawia.coriolan.data.importer.file

import android.content.Context
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.data.importer.DataImporter
import com.ashalmawia.coriolan.model.Deck
import java.io.File

class ImporterFromFile : DataImporter {

    override fun label(): Int {
        return R.string.import_from_file
    }

    override fun launch(context: Context) {
        val intent = EnterFilePathActivity.intent(context)
        context.startActivity(intent)
    }

    fun onFile(context: Context, path: String, deck: Deck) {
        val file = File(path)
        if (!file.exists()) {
            ongoing().onError("File should be located under $path")
            return
        }

        val data = parseDataSafe(file, deck)
        if (data != null) {
            ongoing().onData(context, data)
        }
    }

    private fun parseDataSafe(file: File, deck: Deck): List<CardData>? {
        val parser = FileParser(deck)
        try {
            return parser.parseFile(file)
        } catch (e: ParsingException) {
            ongoing().onError("Failed to parse file, line[" + e.line + "]")
            e.printStackTrace()
            return null
        } catch (e: Exception) {
            ongoing().onError("Error reading the file")
            e.printStackTrace()
            return null
        }
    }
}