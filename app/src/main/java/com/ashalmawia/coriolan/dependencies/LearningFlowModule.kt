package com.ashalmawia.coriolan.dependencies

import android.content.ComponentCallbacks
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.LearningFlow
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Deck
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val SCOPE_LEARNING_FLOW = "scope_learning_flow"

val learningFlowModule = module {

    scope(named(SCOPE_LEARNING_FLOW)) {
        scoped { (exercise: Exercise<in State, *>, deck: Deck, studyOrder: StudyOrder, listener: LearningFlow.Listener<in State>) ->
            LearningFlow(get(), get(), deck, studyOrder, exercise, get(), listener)
        }
    }
}

fun ComponentCallbacks.learningFlowScope() = getKoin().getOrCreateScope(SCOPE_LEARNING_FLOW, named(SCOPE_LEARNING_FLOW))