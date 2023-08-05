package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.CardInfo
import com.ashalmawia.coriolan.data.backup.DeckInfo
import com.ashalmawia.coriolan.data.backup.DomainInfo
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.util.asDeckId
import com.ashalmawia.coriolan.util.asDomainId

fun domainInfo(id: Long, name: String, origLangId: Long, transLangId: Long) = DomainInfo(id.asDomainId(), name, origLangId, transLangId)
fun deckInfo(id: Long, domainId: Long, name: String) = DeckInfo(id.asDeckId(), domainId.asDomainId(), name)
fun cardInfo(id: Long, deckId: Long, domainId: Long, originalId: Long, translationIds: List<Long>, cardType: CardType?) =
        CardInfo(id, deckId.asDeckId(), domainId.asDomainId(), originalId, translationIds, cardType)
