package com.ashalmawia.coriolan.dependencies

import com.ashalmawia.coriolan.ui.AddEditCardActivity
import com.ashalmawia.coriolan.ui.AddEditDeckActivity
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.erased.instance
import org.kodein.di.newInstance

interface Dependecies {

    val addEditCardActivity: AddEditCardActivity.Dependencies
    val addEditDeckActivity: AddEditDeckActivity.Dependencies
}

class KodeinDependencies : Dependecies {

    private val kodein = Kodein {

    }.direct

    override val addEditCardActivity: AddEditCardActivity.Dependencies
        get() = kodein.newInstance { AddEditCardActivity.Dependencies(instance(), instance()) }

    override val addEditDeckActivity: AddEditDeckActivity.Dependencies
        get() = kodein.newInstance { AddEditDeckActivity.Dependencies(instance()) }
}