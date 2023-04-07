package com.ashalmawia.coriolan.data.storage.sqlite

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_DECK_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_DOMAIN_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_FRONT_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_REVERSE_CARD_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_REVERSE_TERM_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.CARDS_TYPE
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DECKS_DOMAIN_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DECKS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DECKS_NAME
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Extras
import com.ashalmawia.coriolan.model.Term
import com.ashalmawia.coriolan.model.Language
import com.ashalmawia.coriolan.util.*
import org.joda.time.DateTime
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS_LANG_ORIGINAL
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS_LANG_TRANSLATIONS
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.DOMAINS_NAME
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.LANGUAGES_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.LANGUAGES_VALUE
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES_CARD_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES_DUE_DATE
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES_EXERCISE
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.STATES_PERIOD
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS_EXTRAS
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS_LANGUAGE_ID
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteContract.TERMS_VALUE
import com.ashalmawia.coriolan.learning.exercise.sr.ExerciseState
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain

// ---------------- LANGUAGES --------------
fun Cursor.languagesId(alias: String? = null): Long { return long(LANGUAGES_ID, alias) }
fun Cursor.languagesValue(alias: String? = null): String { return string(LANGUAGES_VALUE, alias) }
fun Cursor.language(alias: String? = null): Language {
    return Language(
            languagesId(alias),
            languagesValue(alias)
    )
}

// ---------------- DOMAINS --------------
fun Cursor.domainsId(): Long { return long(DOMAINS_ID) }
fun Cursor.domainsName(): String? { return stringOrNull(DOMAINS_NAME) }
fun Cursor.domainsOriginalLangId(): Long { return long(DOMAINS_LANG_ORIGINAL) }
fun Cursor.domainsTranslationsLangId(): Long { return long(DOMAINS_LANG_TRANSLATIONS) }

// ---------------- TERMS --------------
fun Cursor.termsId(): Long { return long(TERMS_ID) }
fun Cursor.termsValue(): String { return string(TERMS_VALUE) }
fun Cursor.termsLanguageId(): Long { return long(TERMS_LANGUAGE_ID) }
fun Cursor.termsExtras(deserializer: ExtrasDeserializer): Extras {
    val serialized = stringOrNull(TERMS_EXTRAS)
    return deserializer.deserialize(serialized)
}
fun Cursor.term(deserializer: ExtrasDeserializer): Term {
    return Term(
            termsId(),
            termsValue(),
            language(),
            termsExtras(deserializer)
    )
}

// ---------------- DECKS --------------
fun Cursor.decksId(): Long { return long(DECKS_ID) }
fun Cursor.decksName(): String { return string(DECKS_NAME) }
fun Cursor.decksDomainId(): Long { return long(DECKS_DOMAIN_ID) }
fun Cursor.deck(domain: Domain): Deck {
    return Deck(decksId(), domain, decksName())
}

// ---------------- CARDS --------------
fun Cursor.cardsId(): Long { return long(CARDS_ID) }
fun Cursor.cardsFrontId(): Long { return long(CARDS_FRONT_ID) }
fun Cursor.cardsDeckId(): Long { return long(CARDS_DECK_ID) }
fun Cursor.cardsDomainId(): Long { return long(CARDS_DOMAIN_ID) }
fun Cursor.cardsCardType(): CardType {
    val type = string(CARDS_TYPE)
    return CardType.fromValue(type)
}
fun Cursor.card(domain: Domain, reverse: Map<Long, List<Term>>): Card {
    val id = cardsId()
    return Card(
            id,
            cardsDeckId(),
            domain,
            cardsCardType(),
            term(CreateContentValues),
            reverse[id]!!
    )
}

// ---------------- CARDS REVERSE --------------
fun Cursor.reverseCardId(): Long { return long(CARDS_REVERSE_CARD_ID) }
fun Cursor.reverseTermId(): Long { return long(CARDS_REVERSE_TERM_ID) }

// ---------------- STATES --------------
fun Cursor.statesCardId(): Long { return long(STATES_CARD_ID) }

fun Cursor.statesExerciseId(): ExerciseId {
    val value = string(STATES_EXERCISE)
    return ExerciseId.fromValue(value)
}
fun Cursor.statesDateDue(): DateTime { return date(STATES_DUE_DATE) }
fun Cursor.statesPeriod(): Int { return int(STATES_PERIOD) }
fun Cursor.statesHasSavedExerciseState(): Boolean { return !isNull(STATES_EXERCISE) }
fun Cursor.exerciseState(): ExerciseState {
    return ExerciseState(statesDateDue(), statesPeriod())
}

// ----------------------------------------------

fun ContentValues.put(key: String, value: DateTime) {
    put(key, value.toDate().time)
}
fun ContentValues.getAsDate(key: String): DateTime? {
    val timestamp = getAsLong(key)
    return if (timestamp == null) null else DateTime(timestamp)
}

fun SQLiteDatabase.insertOrUpdate(table: String, cv: ContentValues): Long {
    return insertWithOnConflict(table, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
}