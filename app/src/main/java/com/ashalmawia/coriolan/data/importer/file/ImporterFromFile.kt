package com.ashalmawia.coriolan.data.importer.file

import android.content.Context
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.LanguagesRegistry
import com.ashalmawia.coriolan.data.importer.DataImporter
import java.io.File

class ImporterFromFile : DataImporter {

    val parser = FileParser(DecksRegistry.get(), LanguagesRegistry)

    override fun label(): Int {
        return R.string.import_from_file
    }

    override fun launch(context: Context) {
        val intent = EnterFilePathActivity.intent(context)
        context.startActivity(intent)
    }

    fun onFile(context: Context, path: String) {
        val file = File(path)
        if (!file.exists()) {
            ongoing().onError("File should be located under $path")
            return
        }

        try {
            val data = parser.parseFile(file)
            ongoing().onData(context, data)
        } catch (e: ParsingException) {
            ongoing().onError("Failed to parse file, line[" + e.line + "]")
            e.printStackTrace()
        } catch (e: Exception) {
            ongoing().onError("Error reading the file")
            e.printStackTrace()
        }
    }
}