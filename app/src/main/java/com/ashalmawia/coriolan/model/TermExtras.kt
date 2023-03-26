package com.ashalmawia.coriolan.model

data class TermExtras(val term: Term, val map: Map<ExtraType, TermExtra>) {
    val transcription = map[ExtraType.TRANSCRIPTION]?.value
}