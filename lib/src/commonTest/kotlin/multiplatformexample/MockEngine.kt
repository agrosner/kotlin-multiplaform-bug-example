package com.grosner.multiplatformexample

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.http.ContentType
import io.ktor.http.fullPath
import io.ktor.http.headersOf

const val mockUrl = "https://example.org"

val mockEnUrl: String
    get() = "$mockUrl/en.json"
val mockEsUrl: String
    get() = "$mockUrl/es.json"

/**
 * Returns a useable mock engine to send fake http calls through KTOR chain.
 * @param enJson - provide english json string as each platform loads it differently
 * @param esJson - provide spanish json string as each platform loads it differently
 */
fun mockClient(enJson: String,
               esJson: String) = HttpClient(MockEngine) {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
    engine {
        addHandler { request ->
            when (request.url.toString()) {
                mockEnUrl -> {
                    val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                    respond(enJson, headers = responseHeaders)
                }
                mockEsUrl -> {
                    val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                    respond(esJson, headers = responseHeaders)
                }
                else -> error("Unhandled ${request.url.fullPath}")
            }
        }
    }
}
