package com.chesire.nekome.kaspresso.mock

import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer

object MockWebServerHolder {
    private var mockWebServer: MockWebServer? = null
    
    fun start() {
        if (mockWebServer == null) {
            mockWebServer = MockWebServer().apply {
                dispatcher = MockedDispatcher()
                start(8080)
            }
        }
    }
    
    fun shutdown() {
        mockWebServer?.shutdown()
        mockWebServer = null
    }
    
    val baseUrl: HttpUrl
        get() = mockWebServer?.url("/") 
            ?: throw IllegalStateException("MockWebServer not started")
    
    val dispatcher: MockedDispatcher
        get() = mockWebServer?.dispatcher as? MockedDispatcher
            ?: throw IllegalStateException("MockWebServer not started or dispatcher not set")
}