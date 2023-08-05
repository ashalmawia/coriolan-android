package com.ashalmawia.coriolan.data.storage.sqlite.payload

import com.ashalmawia.coriolan.util.timespamp
import org.joda.time.DateTime

data class CardPayload(val translationIds: List<TermId>, val dateAdded: Long = DateTime.now().timespamp) {

    fun dateAdded() = DateTime(dateAdded)
    fun translationsIds() = translationIds.map { it.toTermId() }
}

data class TermId(val id: Long) {
    fun toTermId() = com.ashalmawia.coriolan.model.TermId(id)
}

fun com.ashalmawia.coriolan.model.TermId.toPayloadTermId() = TermId(value)