import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

val kotlin_version: String by extra

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("kotlinx-serialization") version "1.3.41"
}

repositories {
    google()
    gradlePluginPortal()
    jcenter()
}

group = "com.grosner.multiplatformexample"
version = "0.0.1"

apply(plugin = "maven-publish")

val ktorVersion = "1.2.3-rc"
val coroutinesVersion = "1.3.0-RC"

kotlin {
    js {
        browser {
        }
        nodejs {
        }
    }
    iosArm64()
    iosX64()
    android {

    }
    // For ARM, should be changed to iosArm32 or iosArm64
    // For Linux, should be changed to e.g. linuxX64
    // For MacOS, should be changed to e.g. macosX64
    // For Windows, should be changed to e.g. mingwX64
    sourceSets {
        val iosMain = create("iosMain")
        val iosTest = create("iosTest")
        val androidMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(kotlin("reflect"))
            }
        }
        val androidTest by getting { dependsOn(androidMain) }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        val iosX64Main by getting { dependsOn(iosMain) }
        val iosX64Test by getting { dependsOn(iosTest) }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosArm64Test by getting { dependsOn(iosTest) }
    }
}

dependencies {
    commonMainApi(kotlin("stdlib-common"))
    commonMainApi("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    commonMainApi("io.ktor:ktor-client-core:$ktorVersion")
    platformApi("io.ktor:ktor-client:$ktorVersion", listOf("ios", "android", "js"))

    platformApi("io.ktor:ktor-client-serialization:$ktorVersion", listOf("jvm", "js", "common", "iosArm64", "iosX64"),
            mapOf("common" to ""))
    platformApi("io.ktor:ktor-client-json:$ktorVersion",
            listOf("native", "jvm", "js", "common"),
            mapOf("common" to ""))



    platformApi("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.1",
            listOf("native", "js", "android", "common"),
            mapOf("android" to ""))

    platformApi("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion", listOf("native", "js", "common"))

    commonTestImplementation(kotlin("test-common"))
    commonTestImplementation(kotlin("test-annotations-common"))
    commonTestImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    androidTestImplementation(kotlin("test"))
    androidTestImplementation(kotlin("test-junit"))
    androidTestImplementation("androidx.test:core:1.2.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("org.robolectric:robolectric:4.0")
    platformApi("io.ktor:ktor-client-mock:$ktorVersion", listOf("jvm", "js", "native"), testOnly = true)
}

fun DependencyHandler.platformApi(artifact: String, platforms: List<String>,
                                  suffixMappings: Map<String, String> = mapOf(),
                                  nativeIsIos: Boolean = true, jvmIsAndroid: Boolean = true,
                                  testOnly: Boolean = false) {
    val (group, name, version) = artifact.split(":", limit = 3)
    platforms.forEach { platform ->
        val suffix = suffixMappings.getOrElse(platform) { platform }.toLowerCase().let { if (it.isNotEmpty()) "-$it" else it }
        val combined = "$group:$name$suffix:$version"
        val compilation = when (platform) {
            "native" -> if (nativeIsIos) "ios" else platform
            "jvm" -> if (jvmIsAndroid) "android" else platform
            else -> platform
        }
        if (!testOnly) {
            add("${compilation}MainApi", combined)
        }
        add("${compilation}TestImplementation", combined)
    }
}

val copyJSResourcesIntoBundle = tasks.register<Copy>("copyJSResourcesIntoBundle") {
    inputs.files("${projectDir}/src/commonTest/resources")
    println("RUNNNIng task")
    from("${projectDir}/src/commonTest/resources")
    into("${rootDir}/build/js/packages/lexicographer-lib-test/kotlin/resources")
}
tasks.getByName("compileTestKotlinJs") {
    finalizedBy(copyJSResourcesIntoBundle)
}

val iosTest = tasks.register("iosTest") {
    dependsOn(tasks.getByName("linkDebugTestIosX64"))
    doLast {
        val testBinaryPath = kotlin.targets.getByName<KotlinNativeTarget>("iosSim").binaries.getTest("DEBUG").outputFile.absolutePath
        exec {
            commandLine("xcrun", "simctl", "spawn", "iPhone Xʀ", testBinaryPath)
        }
    }
}
tasks.getByName("allTests").dependsOn(iosTest)

android {
    compileSdkVersion(28)

    defaultConfig {
        minSdkVersion(15)
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
    sourceSets["test"].apply {
        resources.srcDirs("src/androidTest/resources")
        assets.srcDirs("src/androidTest/assets")
    }
}
// workaround for https://youtrack.jetbrains.com/issue/KT-27170
configurations {
    create("compileClasspath")
}
