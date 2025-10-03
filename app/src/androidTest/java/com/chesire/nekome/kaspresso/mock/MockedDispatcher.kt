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

        val responsesQueue = session[MockedRequest(requestPath, requestMethod)]
        if (responsesQueue.isNullOrEmpty()) {
            return MockResponse().setResponseCode(404)
        }

        val response = responsesQueue.first()
        if (!response.repeatable) {
            responsesQueue.removeFirst()
        }

        val responseString = response.json
            ?.takeIf { it.isNotEmpty() }
            ?: getStringFromJson(response.jsonFile!!)

        return MockResponse()
            .setResponseCode(response.httpCode)
            .setBody(responseString)
    }

    private fun getStringFromJson(fileName: String): String {
        val inputStream = this.javaClass.classLoader!!.getResourceAsStream(fileName)
            ?: FileInputStream(fileName)
        return InputStreamReader(inputStream).use { it.readText() }
    }
}