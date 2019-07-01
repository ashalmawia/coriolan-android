package com.ashalmawia.coriolan.data.importer

object JsonCartDataParserTestData {

    val wordsExpected = listOf(
            JsonCardData("shrimp", null, listOf("креветка")),
            JsonCardData("rocket", "/ˈrɒkɪt \$ ˈrɑː-/", listOf("ракета", "салют")),
            JsonCardData("spring", "/sprɪŋ/", listOf("пружина", "весна", "источник"))
    )

    val wordsJson = """{
      "cards": [
        {
          "original": "shrimp",
          "translations": ["креветка"]
        },
        {
          "original": "rocket",
          "transcription": "/ˈrɒkɪt $ ˈrɑː-/",
          "translations": ["ракета", "салют"]
        },
        {
          "original": "spring",
          "transcription": "/sprɪŋ/",
          "translations": ["пружина", "весна", "источник"]
        }
      ]
    }"""

    val expressionsJson = """{
      "cards": [
        {
          "original": "Мне нужно идти.",
          "translations": ["I have to go."]
        }
      ]
    }"""

    val expressionsExpected = listOf(
            JsonCardData("Мне нужно идти.", null, listOf("I have to go."))
    )

    val jsonWithTypo = """{
      "cards": [
        {
          "original": "rocket",
          "transpiption": "/ˈrɒkɪt $ ˈrɑː-/",
          "transldations": ["ракета", "салют"]
        }
      ]
    }"""

    val jsonNoTranslations = """{
      "cards": [
        {
          "original": "rocket",
          "transpiption": "/ˈrɒkɪt $ ˈrɑː-/"
        }
      ]
    }"""

    val jsonNoOriginal = """{
      "cards": [
        {
          "transpiption": "/ˈrɒkɪt $ ˈrɑː-/",
          "translations": ["ракета", "салют"]
        }
      ]
    }"""

    val jsonNoRoot = """{
      "original": "Мне нужно идти.",
      "translations": ["I have to go."]
    }"""
}