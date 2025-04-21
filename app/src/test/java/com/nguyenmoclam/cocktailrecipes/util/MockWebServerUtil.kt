package com.nguyenmoclam.cocktailrecipes.util

import com.google.gson.Gson
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import java.nio.charset.StandardCharsets

/**
 * Utility for setting up MockWebServer for network tests
 */
object MockWebServerUtil {

    /**
     * Enqueues a mock response with JSON from a file
     * @param server The MockWebServer instance
     * @param fileName The name of the file in the resources folder
     * @param responseCode The HTTP response code to return
     */
    fun enqueueResponse(
        server: MockWebServer,
        fileName: String,
        responseCode: Int = 200
    ) {
        val inputStream = MockWebServerUtil::class.java.classLoader?.getResourceAsStream(fileName)
        
        val source = inputStream?.source()?.buffer() ?: throw IllegalArgumentException(
            "Could not find file: $fileName"
        )
        
        server.enqueue(
            MockResponse()
                .setResponseCode(responseCode)
                .setBody(source.readString(StandardCharsets.UTF_8))
        )
    }
    
    /**
     * Enqueues a mock response with JSON from an object
     * @param server The MockWebServer instance
     * @param response The object to convert to JSON
     * @param responseCode The HTTP response code to return
     */
    fun enqueueResponse(
        server: MockWebServer,
        response: Any,
        responseCode: Int = 200
    ) {
        server.enqueue(
            MockResponse()
                .setResponseCode(responseCode)
                .setHeader("Content-Type", "application/json")
                .setBody(Gson().toJson(response))
        )
    }
    
    /**
     * Enqueues an error response
     * @param server The MockWebServer instance
     * @param responseCode The HTTP error code to return
     */
    fun enqueueErrorResponse(
        server: MockWebServer,
        responseCode: Int = 404
    ) {
        server.enqueue(
            MockResponse()
                .setResponseCode(responseCode)
                .setBody("{\"message\": \"Error occurred\"}")
        )
    }
} 