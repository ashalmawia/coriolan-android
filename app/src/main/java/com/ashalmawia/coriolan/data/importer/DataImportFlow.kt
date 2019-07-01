package com.ashalmawia.coriolan.data.importer

import android.content.Context
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.model.CardData

interface DataImportFlow {

    var callback: DataImportCallback?

    val importer: DataImporter

    fun start()

    fun onData(data: List<CardData>)

    fun onError(message: String)
}

class DataImportFlowImpl(
        private val context: Context,
        private val decksRegistry: DecksRegistry,
        override val importer: DataImporter
) : DataImportFlow {

    override var callback: DataImportCallback? = null

    override fun start() {
        importer.flow = this
        importer.launch(context)
    }

    override fun onData(data: List<CardData>) {
        // TODO: add generalized confirmation UI

        decksRegistry.addCardsToDeck(data)

        callback?.onSuccess()
        finish()
    }

    override fun onError(message: String) {
        // TODO: add generalized error notification UI
        callback?.onError(message)
        finish()
    }

    private fun finish() {
        importer.flow = null
    }
}

interface DataImportCallback {

    fun onSuccess()

    fun onError(message: String)

}