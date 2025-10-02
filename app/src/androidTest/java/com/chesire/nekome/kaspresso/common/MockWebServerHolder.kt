package com.chesire.nekome.kaspresso.common

import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

object MockWebServerHolder {
    private var server: MockWebServer? = null
    
    fun start() {
        server = MockWebServer().apply {
            start()
            // Минимальный dispatcher - заглушка
            dispatcher = object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    return MockResponse()
                        .setResponseCode(200)
                        .setBody("""{"stub": "response"}""")
                }
            }
        }
    }
    
    val baseUrl: HttpUrl
        get() = server?.url("/") 
            ?: throw IllegalStateException("MockWebServer not started")
    
    fun shutdown() {
        server?.shutdown()
        server = null
    }
}