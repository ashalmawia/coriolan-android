package com.ashalmawia.coriolan.model

class Notion(
        val lang: String,
        val value: String,
        val type: NotionType
) {

    //val original Card;
    //val List<Card> trans;

}

enum class NotionType {
    WORD
}