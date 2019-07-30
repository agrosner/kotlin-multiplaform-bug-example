package com.grosner.multiplatformexample

expect fun <T> runTest(block: suspend () -> T)
