package com.grosner.multiplatformexample

interface LocalizableString {
    val key: String
    fun localize(lexicographer: Lexicographer, vararg substituting: String) = lexicographer.localize(key, *substituting)
}
