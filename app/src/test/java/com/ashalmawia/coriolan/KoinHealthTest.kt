package com.ashalmawia.coriolan

import com.ashalmawia.coriolan.dependencies.koinModules
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class KoinHealthTest : KoinTest {

    @Test
    fun `check Koin modules`() {
        koinApplication {
            androidContext(RuntimeEnvironment.application)
            modules(koinModules)
        }.checkModules()
    }
}