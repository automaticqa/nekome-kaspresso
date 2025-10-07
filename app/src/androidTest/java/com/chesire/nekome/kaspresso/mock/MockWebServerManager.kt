package com.chesire.nekome.kaspresso.mock

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer

const val DEFAULT_200_CODE_RESPONSE = 200
const val HTTP_204_NO_CONTENT = 204

object MockWebServerManager {
    private lateinit var mockWebServer: MockWebServer

    @Volatile private var started = false
    @Volatile private var ready = CountDownLatch(1)

    @Synchronized
    fun start(port: Int = 0) {
        if (started) return

        mockWebServer = MockWebServer().apply {
            dispatcher = MockedDispatcher()
            start(port)
        }

        mockWebServer.url("/")

        started = true
        ready.countDown()
        println("okhttp: MockWebServerManager.start() - MockWebServer started on ${mockWebServer.hostName}:${mockWebServer.port}")
    }

    fun awaitReadyOrThrow(timeoutMs: Long = 5_000) {
        val ok = ready.await(timeoutMs, TimeUnit.MILLISECONDS)
        check(ok && started) { "MockWebServer not ready within ${timeoutMs}ms" }
    }

    val baseUrl: HttpUrl
        get() {
            check(started) { "MockWebServer not started yet" }
            return mockWebServer.url("/")
        }

    fun awaitAndGetBaseUrl(timeoutMs: Long = 5_000): String {
        awaitReadyOrThrow(timeoutMs)
        return baseUrl.toString()
    }

    @Synchronized
    fun shutdown() {
        if (started) {
            mockWebServer.shutdown()
            started = false
            ready = CountDownLatch(1)
        }
    }

    fun mockPost(
        requestPath: String,
        responseJsonFile: String,
        responseCode: Int = DEFAULT_200_CODE_RESPONSE,
        repeatable: Boolean = false,
    ) = mockRequest(POST, requestPath, responseJsonFile, responseCode, repeatable)

    fun mockGet(
        requestPath: String,
        responseJsonFile: String,
        responseCode: Int = DEFAULT_200_CODE_RESPONSE,
        repeatable: Boolean = false,
        queryParams: Map<String, String> = emptyMap()
    ) = mockRequest(GET, requestPath, responseJsonFile, responseCode, repeatable, queryParams)

    fun mockRequest(
        requestMethod: String,
        requestPath: String,
        responseJsonFile: String,
        responseCode: Int = DEFAULT_200_CODE_RESPONSE,
        repeatable: Boolean = false,
        queryParams: Map<String, String> = emptyMap()
    ) {
        mockRequest(
            MockedRequest(requestPath, requestMethod, queryParams),
            MockedResponse(responseJsonFile, responseCode, repeatable)
        )
    }

    fun mockRequestWithJsonResponse(
        requestMethod: String,
        requestPath: String,
        responseJson: String,
        responseCode: Int = DEFAULT_200_CODE_RESPONSE,
        repeatable: Boolean = false,
    ) {
        mockRequest(
            MockedRequest(requestPath, requestMethod),
            MockedResponse(null, responseCode, repeatable, responseJson)
        )
    }

    private fun mockRequest(
        request: MockedRequest,
        response: MockedResponse,
    ) {
        (mockWebServer.dispatcher as MockedDispatcher).mock(request, response)
    }

    const val GET = "GET"
    const val POST = "POST"
    const val DELETE = "DELETE"
}