package com.grosner.multiplatformexample

actual class ResourceHelper(
        /**
         * Specify a path to the JSON directory so the library can find it.
         * Since we cannot dynamically include the locales in the constructor, we must up-front specify where they reside.
         */
        private val localeMap: Map<String, (fileName: String) -> Store>) {
    /**
     * Cache results that are already loaded and return them.
     */
    private val cachedLocaleMap = mutableMapOf<String, Result>()

    actual suspend fun getBundledJSON(fileName: String, forLocale: String): Result {
        val defaultValue = localeMap[forLocale] ?: return null to BundledStoreAccessError.NotFound
        return cachedLocaleMap.getOrPut(forLocale) {
            jsonStringToResult(JSON.stringify(defaultValue(fileName)))
        }
    }
}
