package com.ashalmawia.coriolan.dependencies

import android.content.ComponentCallbacks
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.LearningFlow
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Deck
import org.koin.android.ext.android.getKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val SCOPE_LEARNING_FLOW = "scope_learning_flow"

val learningFlowModule = module {

    scope(named(SCOPE_LEARNING_FLOW)) {
        scoped { (exercise: Exercise<*, *>, deck: Deck, studyOrder: StudyOrder) ->
            LearningFlow(get(), get(), deck, studyOrder, exercise, get())
                    .apply { addFinishListener { close() } }
        }
    }
}

fun ComponentCallbacks.learningFlowScope() = getKoin().getOrCreateScope(SCOPE_LEARNING_FLOW, named(SCOPE_LEARNING_FLOW))