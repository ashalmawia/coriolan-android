package com.ashalmawia.coriolan.dependencies

import android.content.ComponentCallbacks
import com.ashalmawia.coriolan.data.importer.DataImportFlow
import com.ashalmawia.coriolan.data.importer.DataImportFlowImpl
import com.ashalmawia.coriolan.data.importer.DataImporter
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val SCOPE_DATA_IMPORT = "scope_data_import"

val dataImportModule = module {

    scope(named(SCOPE_DATA_IMPORT)) {
        scoped<DataImportFlow> { (importer: DataImporter) -> DataImportFlowImpl(get(), get(), importer) }
    }
}

fun ComponentCallbacks.dataImportScope() = getKoin().getScope(SCOPE_DATA_IMPORT)