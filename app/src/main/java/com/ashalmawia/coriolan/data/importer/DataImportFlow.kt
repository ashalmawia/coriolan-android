package com.ashalmawia.coriolan.data.importer

import android.content.Context
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.importer.file.ImporterFromFile

class DataImportFlow(
        val importer: DataImporter,
        private val callback: DataImportCallback
) {

    companion object {
        private val importers = arrayOf(
                ImporterFromFile()
        )

        fun default(): DataImporter {
            // this is an artificial function which is expected to go with the appearance
            // of the UI for selecting the import method
            // logically, there is no default importing method
            // so it doesn't make sense to create something smarter here so far
            return importers[0]
        }

        var ongoing : DataImportFlow? = null
            private set

        fun start(context: Context, importer: DataImporter, callback: DataImportCallback) {
            val import = DataImportFlow(importer, callback)
            ongoing = import
            import.start(context)
        }

        private fun finish() {
            ongoing = null
        }
    }

    private fun start(context: Context) {
        importer.launch(context)
    }

    fun onData(context: Context, data: List<CardData>) {
        // TODO: add generalized confirmation UI

        DecksRegistry.get(context).addCardsToDeck(data)

        callback.onSuccess()
        finish()
    }

    fun onError(message: String) {
        // TODO: add generalized error notification UI
        callback.onError(message)
        finish()
    }
}

interface DataImportCallback {

    fun onSuccess()

    fun onError(message: String)

}