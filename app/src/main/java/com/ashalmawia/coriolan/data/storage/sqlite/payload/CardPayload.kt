package com.ashalmawia.coriolan.data.storage.sqlite.payload

data class CardPayload(val translationIds: List<TermId>)

data class TermId(val id: Long)