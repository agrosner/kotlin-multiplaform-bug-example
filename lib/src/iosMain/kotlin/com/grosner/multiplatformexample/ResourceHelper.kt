package com.grosner.multiplatformexample

import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.NSJSONReadingAllowFragments
import platform.Foundation.NSJSONSerialization
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL

actual class ResourceHelper {

    actual suspend fun getBundledJSON(fileName: String, forLocale: String): Result {
        val paths = NSBundle.allBundles.mapNotNull { (it as NSBundle).pathForResource(fileName, ofType = "json") }
        val path = paths.getOrNull(0)
        if (path == null) {
            print("[WARNING] ResourceHelper: failed to find bundled string json.")
            return null to BundledStoreAccessError.NotFound
        }

        val data = NSData.dataWithContentsOfURL(NSURL.fileURLWithPath(fileName))
        if (data == null) {
            print("[WARNING] ResourceHelper: failed to convert bundled json to binary.")
            return null to BundledStoreAccessError.BadFormat
        }

        val json = NSJSONSerialization.dataWithJSONObject(data, NSJSONReadingAllowFragments, null)
        @Suppress("UNCHECKED_CAST") val store = json as? Store
        if (store == null) {
            print("[WARNING] ResourceHelper: failed to convert bundled json to json of type [String: [String: String]")
            return null to BundledStoreAccessError.BadJSONType
        }
        return store to null
    }
}
