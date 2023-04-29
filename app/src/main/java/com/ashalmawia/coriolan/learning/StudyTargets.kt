package com.ashalmawia.coriolan.learning

import java.io.Serializable

data class StudyTargets(val new: Int?, val review: Int?) : Serializable {

    fun unlimited() = new == null && review == null
}