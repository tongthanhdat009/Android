// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(libs.gradle)
        classpath(libs.google.services)
    }
    repositories{
        maven("https://www.jitpack.io")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://www.jitpack.io")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}