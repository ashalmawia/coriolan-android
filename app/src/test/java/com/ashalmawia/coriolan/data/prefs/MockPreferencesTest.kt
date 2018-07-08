package com.ashalmawia.coriolan.data.prefs

import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MockPreferencesTest : PreferencesTest() {

    override fun create(): Preferences = MockPreferences()
}