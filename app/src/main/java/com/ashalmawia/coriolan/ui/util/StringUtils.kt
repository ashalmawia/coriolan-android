package com.ashalmawia.coriolan.ui.util

/**
 * Fix for italic text clipped by TextView
 */
fun String?.fixItalicClipping() = if (this.isNullOrBlank()) this else " $this "