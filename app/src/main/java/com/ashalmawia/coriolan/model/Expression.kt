package com.ashalmawia.coriolan.model

class Expression(
        val lang: String,
        val value: String,
        val type: ExpressionType
) {

    //val original Card;
    //val List<Card> trans;

}

enum class ExpressionType {
    WORD
}