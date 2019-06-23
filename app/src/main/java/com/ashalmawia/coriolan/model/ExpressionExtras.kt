package com.ashalmawia.coriolan.model

data class ExpressionExtras(val expression: Expression, val map: Map<ExtraType, ExpressionExtra>) {
    val transcription = map[ExtraType.TRANSCRIPTION]?.value
}