package com.ashalmawia.coriolan.ui.main.decks_list

import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.learning.CardTypeFilter

data class DeckListItem(val deck: Deck, val cardTypeFilter: CardTypeFilter)