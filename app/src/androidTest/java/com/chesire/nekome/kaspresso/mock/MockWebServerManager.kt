package com.chesire.nekome.kaspresso.mock

import okhttp3.mockwebserver.MockWebServer

const val DEFAULT_200_CODE_RESPONSE = 200
const val ERROR_400_CODE_RESPONSE = 400
const val ERROR_403_CODE_RESPONSE = 403

object MockWebServerManager {
    private lateinit var mockWebServer: MockWebServer

    fun start(port: Int = 8080) {
        mockWebServer = MockWebServer()
        mockWebServer.dispatcher = MockedDispatcher()
        mockWebServer.start(port)
    }

    fun shutdown() {
        mockWebServer.shutdown()
    }

    fun mockPost(
        requestPath: String,
        responseJsonFile: String,
        responseCode: Int = DEFAULT_200_CODE_RESPONSE,
        repeatable: Boolean = false,
    ) = mockRequest(POST, requestPath, responseJsonFile, responseCode, repeatable)

    fun mockPut(
        requestPath: String,
        responseJsonFile: String,
        responseCode: Int = DEFAULT_200_CODE_RESPONSE,
        repeatable: Boolean = false,
    ) = mockRequest(PUT, requestPath, responseJsonFile, responseCode, repeatable)

    fun mockGet(
        requestPath: String,
        responseJsonFile: String,
        responseCode: Int = DEFAULT_200_CODE_RESPONSE,
        repeatable: Boolean = false,
    ) = mockRequest(GET, requestPath, responseJsonFile, responseCode, repeatable)

    fun mockRequests(
        requestMethod: String,
        requestPath: String,
        responseJsonFiles: List<String>,
        responseCode: Int = DEFAULT_200_CODE_RESPONSE,
        repeatable: Boolean = false,
    ) = responseJsonFiles.forEach { file ->
        mockRequest(
            requestMethod,
            requestPath,
            file,
            responseCode,
            repeatable
        )
    }

    fun mockRequests(
        requestMethod: String,
        requestPath: String,
        responseMappings: Map<String, Int>,
        repeatable: Boolean = false,
    ) {
        responseMappings.forEach { (file, code) ->
            mockRequest(
                requestMethod,
                requestPath,
                file,
                code,
                repeatable
            )
        }
    }

    fun mockRequest(
        requestMethod: String,
        requestPath: String,
        responseJsonFile: String,
        responseCode: Int = DEFAULT_200_CODE_RESPONSE,
        repeatable: Boolean = false,
    ) {
        mockRequest(
            MockedRequest(requestPath, requestMethod),
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
    const val PUT = "PUT"
}