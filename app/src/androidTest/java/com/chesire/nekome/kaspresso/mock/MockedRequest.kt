package com.chesire.nekome.kaspresso.mock

data class MockedRequest(
    val path: String,
    val method: String,
    val queryParams: Map<String, String> = emptyMap()
)