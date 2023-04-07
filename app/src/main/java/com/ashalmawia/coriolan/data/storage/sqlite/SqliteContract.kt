package com.ashalmawia.coriolan.data.storage.sqlite

object SqliteContract {

    const val LANGUAGES = "Languages"

    const val LANGUAGES_ID = "Lang_id"
    const val LANGUAGES_VALUE = "Lang_Value"

// -----------------------------------------------

    const val DOMAINS = "Domains"

    const val DOMAINS_ID = "Domains_id"
    const val DOMAINS_NAME = "Domains_Name"
    const val DOMAINS_LANG_ORIGINAL = "Domains_LangOrig"
    const val DOMAINS_LANG_TRANSLATIONS = "Domains_LangTran"

// -----------------------------------------------

    const val TERMS = "Terms"

    const val TERMS_ID = "Terms_id"
    const val TERMS_VALUE = "Terms_Value"
    const val TERMS_LANGUAGE_ID = "Terms_Lang"
    const val TERMS_EXTRAS = "Terms_Extras"

// -----------------------------------------------

    const val DECKS = "Decks"

    const val DECKS_ID = "Decks_id"
    const val DECKS_NAME = "Decks_Name"
    const val DECKS_DOMAIN_ID = "Decks_Domain"

// -----------------------------------------------

    const val CARDS = "Cards"

    const val CARDS_ID = "Cards_id"
    const val CARDS_FRONT_ID = "Cards_FrontId"
    const val CARDS_DECK_ID = "Cards_DeckId"
    const val CARDS_DOMAIN_ID = "Cards_DomainId"
    const val CARDS_TYPE = "Cards_Type"

// -----------------------------------------------

    const val CARDS_REVERSE = "Reverse"

    const val CARDS_REVERSE_CARD_ID = "Reverse_CardId"
    const val CARDS_REVERSE_TERM_ID = "Reverse_TermId"

// -----------------------------------------------

    const val STATES = "States"

    const val STATES_CARD_ID = "States_CardId"
    const val STATES_EXERCISE = "States_Exercise"
    const val STATES_DUE_DATE = "States_DueDate"
    const val STATES_PERIOD = "States_Period"
}