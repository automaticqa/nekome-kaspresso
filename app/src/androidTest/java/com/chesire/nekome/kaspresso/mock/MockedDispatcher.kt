package com.chesire.nekome.kaspresso.mock

import java.io.FileInputStream
import java.io.InputStreamReader
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class MockedDispatcher : Dispatcher() {
    private val session = HashMap<MockedRequest, ArrayDeque<MockedResponse>>()

    fun mock(
        request: MockedRequest,
        response: MockedResponse,
    ) {
        session.getOrPut(request) { ArrayDeque() }.add(response)
    }

    @Synchronized
    override fun dispatch(request: RecordedRequest): MockResponse {
        val requestPath = request.requestUrl!!.encodedPath
        val requestMethod = request.method!!
        val queryParams = request.requestUrl!!.queryParameterNames.associateWith {
            request.requestUrl!!.queryParameter(it) ?: ""
        }

        val mockedRequest = findMatchingMock(requestPath, requestMethod, queryParams)
        val responsesQueue = session[mockedRequest]

        if (responsesQueue.isNullOrEmpty()) {
            println("okhttp: MockWebServer - No mock found for $requestMethod $requestPath with params $queryParams")
            println("okhttp: Available mocks:")
            session.keys.forEach { mock ->
                println("okhttp:   ${mock.method} ${mock.path} ${mock.queryParams}")
            }
            return MockResponse().setResponseCode(404)
        }

        val response = responsesQueue.first()
        if (!response.repeatable) {
            responsesQueue.removeFirst()
        }

        val responseString = when {
            response.json != null -> response.json
            response.jsonFile != null -> getStringFromJson(response.jsonFile)
            else -> ""
        }

        return MockResponse()
            .setResponseCode(response.httpCode)
            .setBody(responseString)
    }

    private fun findMatchingMock(
        path: String,
        method: String,
        queryParams: Map<String, String>
    ): MockedRequest? {
        return session.keys.find { mockedRequest ->
            val pathMatch = mockedRequest.path == path
            val methodMatch = mockedRequest.method == method
            val queryMatch = mockedRequest.queryParams.isEmpty() ||
                            mockedRequest.queryParams.all { (key, value) ->
                                queryParams[key] == value
                            }

            pathMatch && methodMatch && queryMatch
        }
    }

    private fun getStringFromJson(fileName: String): String {
        val inputStream = this.javaClass.classLoader!!.getResourceAsStream(fileName)
            ?: FileInputStream(fileName)
        return InputStreamReader(inputStream).use { it.readText() }
    }
}
