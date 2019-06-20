package com.ashalmawia.coriolan.data.importer

import android.content.Context
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.currentDomain

class DataImportFlow(
        val importer: DataImporter,
        private val callback: DataImportCallback
) {

    companion object {

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

        DecksRegistry.get(context, currentDomain()).addCardsToDeck(data)

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