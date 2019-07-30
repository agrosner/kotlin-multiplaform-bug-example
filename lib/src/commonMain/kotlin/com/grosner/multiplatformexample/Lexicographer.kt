package com.grosner.multiplatformexample

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get

typealias Store = Map<String, Map<String, String>>
typealias Result = Pair<Store?, BundledStoreAccessError?>

internal data class Storage(var bundledStore: Store = mapOf(), var downloadedStore: Store = mapOf())

enum class BundledStoreAccessError {
    NotFound,
    BadFormat,
    BadJSONType
}

class Lexicographer(private val resourceHelper: ResourceHelper,
                    private val client: HttpClient = HttpClient {
                        install(JsonFeature) {
                            serializer = KotlinxSerializer()
                        }
                    }) {

    private val storage: Storage = Storage()

    fun localize(key: String, vararg substituting: String): String? {
        val path = key.split(".")
        if (path.size < 2) {
            throw IllegalArgumentException("Invalid path key specified. It should be \"epic\".\"name\"")
        }
        val (epic, name) = path
        val epicMap = storage.downloadedStore.getOrElse(epic, {
            storage.bundledStore[epic]
        }) ?: return null

        val value = epicMap[name] ?: return null
        val matches = "\\{.*?\\}".toRegex().findAll(value)
        var returnVar = value
        matches.map { it.groups }.flatten().filterNotNull().withIndex().forEach { (index, group) ->
            returnVar = returnVar.replace(group.value, substituting[index])
        }
        return returnVar
    }

    /**
     * Request to update the apps internal storage.
     */
    suspend fun update(locale: String, fromUrl: String? = null): Result {
        val bundled = bundledStore(forLocale = locale)
        storage.bundledStore = bundled.first ?: mapOf()

        if (fromUrl != null) {
            val (store, error) = getRemoteStore(fromUrl = fromUrl)
            if (store != null) {
                storage.downloadedStore = store
            } else if (error != null) {
                // TODO: error
            }
            return store to error
        } else {
            return bundled
        }
    }

    /**
     * Retrieve the bundled string store from the project.
     */
    suspend fun bundledStore(forLocale: String): Result = resourceHelper.getBundledJSON("localized/$forLocale.json", forLocale)

    private suspend fun getRemoteStore(fromUrl: String): Result {
        try {
            val response = client.get<String>(fromUrl)
            return jsonStringToResult(response)
        } catch (e: Exception) {
            // error, TODO: surface somewhere?
            return null to BundledStoreAccessError.NotFound
        }
    }
}
