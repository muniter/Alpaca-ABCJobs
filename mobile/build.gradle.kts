// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    kotlin("kapt") version "1.9.10"
    id("jacoco") apply true
    id ("androidx.navigation.safeargs") version "2.5.3" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jacoco:org.jacoco.core:0.8.6")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3")
    }

}


