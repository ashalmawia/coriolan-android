package com.ashalmawia.coriolan.learning.scheduler

import com.ashalmawia.coriolan.model.Card

data class CardWithState<out T : State>(val card: Card, val state: T)