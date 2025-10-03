package com.chesire.nekome.kaspresso.mock

data class MockedResponse(
    val jsonFile: String? = null,
    val httpCode: Int,
    val repeatable: Boolean,
    val json: String? = null,
)