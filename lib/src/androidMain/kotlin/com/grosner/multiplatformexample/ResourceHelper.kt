package com.grosner.multiplatformexample

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

actual class ResourceHelper(private val applicationContext: Context) {

    actual suspend fun getBundledJSON(fileName: String, forLocale: String): Result = try {
        withContext(Dispatchers.IO) {
            val file = InputStreamReader(applicationContext.assets.open(fileName)).buffered().readText()
            jsonStringToResult(file)
        }
    } catch (e: Exception) {
        Log.e("ResourceHelper", "Failed to parse JSON", e)
        null to BundledStoreAccessError.BadJSONType
    }
}
