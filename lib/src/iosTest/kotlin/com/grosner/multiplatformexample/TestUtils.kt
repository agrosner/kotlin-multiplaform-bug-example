package com.grosner.multiplatformexample
import kotlinx.coroutines.runBlocking

actual fun <T> runTest(block: suspend () -> T) {
    runBlocking { block() }
}

