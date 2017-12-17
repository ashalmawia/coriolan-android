package com.ashalmawia.coriolan.data.importer.file

import android.content.Context
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.importer.DataImporter
import java.io.File

class ImporterFromFile : DataImporter {

    override fun label(): Int {
        return R.string.import_from_file
    }

    override fun launch(context: Context) {
        val intent = EnterFilePathActivity.intent(context)
        context.startActivity(intent)
    }

    fun onFile(path: String) {
        val file = File(path)
        if (!file.exists()) {
            ongoing().onError("File should be located under $path")
            return
        }

        try {
            val data = FileParser.parseFile(file)
            ongoing().onData(data)
        } catch (e: ParsingException) {
            ongoing().onError("Failed to parse file, line[" + e.line + "]")
        } catch (e: Exception) {
            ongoing().onError("Error reading the file")
        }
    }
}