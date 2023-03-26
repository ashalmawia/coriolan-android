package com.ashalmawia.coriolan.data.logbook.sqlite

import com.ashalmawia.coriolan.data.logbook.LogbookPayload
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

class LogbookPayloadSerializer {

    private val objectMapper = jacksonObjectMapper()

    fun serializeLogbookPayload(payload: LogbookPayload): String {
        return objectMapper.writeValueAsString(payload)
    }

    fun deserializeLogbookPayload(payload: String): LogbookPayload {
        return objectMapper.readValue(payload, LogbookPayload::class.java)
    }
}