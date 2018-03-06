package com.ashalmawia.coriolan.data.prefs

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SharedPreferencesTest : PreferencesTest() {

    override fun create(): Preferences = SharedPreferencesImpl(RuntimeEnvironment.application)
}