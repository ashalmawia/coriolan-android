package com.ashalmawia.coriolan.util

import org.joda.time.DateTime

fun DateTime.midnight() = withTime(0, 0, 0, 0)

fun DateTime.endOfDay() = withTime(23, 59, 59, 0)