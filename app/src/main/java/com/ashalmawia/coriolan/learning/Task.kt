package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.model.Card

data class Task(val card: Card, val state: State, val exercise: Exercise)