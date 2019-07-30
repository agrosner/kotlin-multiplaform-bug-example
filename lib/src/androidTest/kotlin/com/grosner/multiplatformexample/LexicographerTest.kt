package com.grosner.multiplatformexample

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith
import java.io.InputStreamReader
import kotlin.test.Test
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
class LexicographerTest : BaseLexicographerTest(lexicographerFn = {
    Lexicographer(ResourceHelper(ApplicationProvider.getApplicationContext()),
            client = mockClient(
                    enJson = InputStreamReader(ApplicationProvider.getApplicationContext<Context>().assets.open("localized/en.json")).buffered().readText(),
                    esJson = InputStreamReader(ApplicationProvider.getApplicationContext<Context>().assets.open("localized/es.json")).buffered().readText()
            ))
}) {

    @Test
    fun testBundledResources() {
        runBlocking {
            val (store, error) = lexicographer.bundledStore("en")
            assert(error == null) {
                "error was $error"
            }
            assertNotNull(store)
            assert(store.isNotEmpty())
        }
    }

    @Test
    fun testRemoteResource() {
        runBlocking {
            val (store, error) = lexicographer.update("en", fromUrl = mockEnUrl)
            assert(error == null) {
                "error was $error"
            }
            assertNotNull(store)
            assert(store.isNotEmpty())
        }
    }
}
