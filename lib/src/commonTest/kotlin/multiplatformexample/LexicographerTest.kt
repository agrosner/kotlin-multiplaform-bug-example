package com.grosner.multiplatformexample

import StringPath
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class BaseLexicographerTest(lexicographerFn: () -> Lexicographer) {
    protected val lexicographer: Lexicographer by lazy { lexicographerFn() }

    @Test
    fun testCanRetrievePlainValue() = runTest {
        lexicographer.update("en", fromUrl = mockEnUrl)
        val value = StringPath.Loading_Header.localize(lexicographer)
        assertEquals("Loading...", value)
    }

    @Test
    fun testCanRetrieveTemplateValue() = runTest {
        lexicographer.update("en", fromUrl = mockEnUrl)
        val value = StringPath.Loading_Success_title.localize(lexicographer, "Andrew", "$50.00")
        assertEquals("Thank you for waiting, Andrew. You now owe $50.00.", value)
    }
}
