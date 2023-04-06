package com.ashalmawia.coriolan.learning

import org.joda.time.LocalDate
import org.joda.time.LocalTime

fun mockToday(): LearningDay = LocalDate.now().toDateTime(LocalTime(4, 0))