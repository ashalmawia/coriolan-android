package com.ashalmawia.coriolan.data.importer

import com.ashalmawia.coriolan.data.importer.file.ImporterFromFile

interface ImporterRegistry {

    companion object {

        private var instance: ImporterRegistry? = null

        fun get() = instance ?: ImporterRegistryImpl.also { instance = it }
    }

    // this is an artificial function which is expected to go with the appearance
    // of the UI for selecting the import method
    // logically, there is no default importing method
    // so it doesn't make sense to create something smarter here so far
    fun default(): DataImporter
}

object ImporterRegistryImpl : ImporterRegistry {

    private val importers = arrayOf(
            ImporterFromFile()
    )

    override fun default() = importers[0]
}