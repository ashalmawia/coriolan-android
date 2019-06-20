package com.ashalmawia.coriolan.data

import com.ashalmawia.coriolan.model.Domain

var currentDomain: Domain? = null

fun currentDomain() = currentDomain ?: throw IllegalStateException("domain expected to have beeen initialized was not")