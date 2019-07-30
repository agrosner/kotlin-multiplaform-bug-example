package com.grosner.multiplatformexample

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@JsNonModule
@JsModule("./resources/localized/en.json")
external val localizedEn: Store

@JsNonModule
@JsModule("./resources/localized/es.json")
external val localizedEs: Store

class LexicographerTest : BaseLexicographerTest(lexicographerFn = {
    Lexicographer(ResourceHelper(localeMap = mapOf(
            "en" to { _ -> localizedEn },
            "es" to { _ -> localizedEs }
    )),
            client = mockClient(
                    enJson = JSON.stringify(localizedEn),
                    esJson = JSON.stringify(localizedEs)
            ))
}) {

    @Test
    fun testBundledResources() = runTest {
        val res = lexicographer.bundledStore("en")
        val (store, error) = res
        assertNull(error, "error was $error")
        assertNotNull(store)
        assertTrue(store.isNotEmpty())
    }

    @Test
    fun testRemoteResource() = runTest {
        val (store, error) = lexicographer.update("en", fromUrl = mockEnUrl)
        assertNull(error, "error was $error")
        assertNotNull(store)
        assertTrue(store.isNotEmpty())
    }
}
