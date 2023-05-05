package com.ashalmawia.coriolan.data.storage.sqlite.payload

import com.ashalmawia.coriolan.util.timespamp
import org.joda.time.DateTime

data class CardPayload(val translationIds: List<TermId>, val dateAdded: Long = DateTime.now().timespamp)

data class TermId(val id: Long)