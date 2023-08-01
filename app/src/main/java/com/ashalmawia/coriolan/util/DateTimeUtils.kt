package com.ashalmawia.coriolan.util

import org.joda.time.DateTime

fun DateTime.midnight() = withTime(0, 0, 0, 0)