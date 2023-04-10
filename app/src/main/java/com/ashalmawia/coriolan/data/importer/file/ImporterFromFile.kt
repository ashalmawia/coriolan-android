package com.ashalmawia.coriolan.data.importer.file

import android.content.Context
import android.util.Log
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.importer.DataImportFlow
import com.ashalmawia.coriolan.data.importer.DataImporter
import com.ashalmawia.coriolan.data.importer.JsonCardData
import com.ashalmawia.coriolan.model.CardData
import com.ashalmawia.coriolan.model.Deck
import java.io.File

private const val TAG = "ImporterFromFile"

class ImporterFromFile : DataImporter {

    override var flow: DataImportFlow? = null

    override fun label(): Int {
        return R.string.import_from_file
    }

    override fun launch(context: Context) {
        //TODO
//        val intent = EnterFilePathActivity.intent(context)
//        context.startActivity(intent)
    }

    fun onFile(path: String, deck: Deck) {
        val file = File(path)
        if (!file.exists()) {
            flow?.onError("File should be located under $path")
            return
        }

        val data = parseDataSafe(file)
        if (data != null) {
            flow?.onData(data.map { CardData(it.original, it.transcription, it.translations, deck) })
        }
    }

    private fun parseDataSafe(file: File): List<JsonCardData>? {
        val parser = JsonCardDataParser()
        return try {
            val text = file.readText()
            parser.parse(text)
        } catch (e: ParsingException) {
            flow?.onError("Failed to parse file, line[" + e.line + "]")
            Log.e(TAG, "failed to parse file", e)
            null
        } catch (e: Exception) {
            flow?.onError("Error reading the file")
            Log.e(TAG, "failed to parse file", e)
            null
        }
    }
}