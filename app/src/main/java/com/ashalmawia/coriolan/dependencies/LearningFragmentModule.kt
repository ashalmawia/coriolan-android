package com.ashalmawia.coriolan.dependencies

import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.ui.BeginStudyListener
import com.ashalmawia.coriolan.ui.DataFetcher
import com.ashalmawia.coriolan.ui.DecksAdapter
import org.koin.core.scope.Scope
import org.koin.dsl.module

val learningFragmentModule = module {

    factory { (activityScope: Scope, exercise: Exercise<*, *>, dataFetcher: DataFetcher, beginStudyListener: BeginStudyListener) ->
        DecksAdapter(
                get(),
                get(),
                exercise,
                dataFetcher,
                beginStudyListener,
                activityScope.get()
        )
    }
}