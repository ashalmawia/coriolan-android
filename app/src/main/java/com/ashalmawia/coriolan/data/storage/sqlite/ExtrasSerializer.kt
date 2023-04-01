package com.ashalmawia.coriolan.data.storage.sqlite

import com.ashalmawia.coriolan.model.Extras

interface ExtrasDeserializer {

    fun deserialize(value: String?): Extras
}