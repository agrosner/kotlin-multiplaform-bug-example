import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        gradlePluginPortal()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.4.2")
    }
}

plugins {
    kotlin("multiplatform") version "1.3.41"
}

subprojects {
    tasks.withType<KotlinCompile> {
        kotlinOptions.freeCompilerArgs = listOf("-Xuse-experimental=kotlinx.serialization.ImplicitReflectionSerializer",
                "-Xuse-experimental=kotlinx.serialization.UnstableDefault")
    }
}
