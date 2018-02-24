package com.ashalmawia.coriolan.data.prefs

class MockPreferences : Preferences {

    private var defaultDeckId: Long? = null
    override fun getDefaultDeckId(): Long? {
        return defaultDeckId
    }

    override fun setDefaultDeckId(id: Long) {
        defaultDeckId = id
    }

    override fun getOriginalLanguageId(): Long? {
        return 1L;
    }

    override fun setOriginalLanguageId(id: Long) {
    }

    override fun getTranslationsLanguageId(): Long? {
        return 1L;
    }

    override fun setTranslationsLanguageId(id: Long) {
    }
}