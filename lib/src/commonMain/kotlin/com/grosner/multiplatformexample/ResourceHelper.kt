package com.grosner.multiplatformexample

import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject


/**
 * Utilizes native lookups to retrieve bundled json and return as a map.
 */
expect class ResourceHelper {

    suspend fun getBundledJSON(fileName: String, forLocale: String): Result
}

@UnstableDefault
fun jsonStringToResult(json: String): Result = try {
    // TODO: is there a better conversion?
    val fromJson = Json.parse(JsonObject.serializer(), json).toMap()
            .map { (key, value) ->
                key to value.jsonObject.toMap()
                        .map { (key, value) -> key to value.primitive.content }.toMap()
            }.toMap()
    fromJson to null
} catch (e: Exception) {
    println("ResourceHelper Failed to parse JSON ${e.message}")
    null to BundledStoreAccessError.BadJSONType
}
