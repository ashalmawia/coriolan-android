package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.Task

interface Mutation {

    fun apply(tasks: List<Task>): List<Task>
}