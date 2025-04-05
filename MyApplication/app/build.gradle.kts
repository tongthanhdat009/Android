import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.project.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.project.myapplication"
        minSdk = 24
        //noinspection EditedTargetSdkVersion
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures{
        viewBinding=true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.material.v180)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.recyclerview)
    implementation(libs.media3.common)
    implementation(libs.media3.exoplayer.hls)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.analytics)
    implementation (libs.firebase.storage)
    implementation (libs.picasso)
    implementation (libs.lifecycle.livedata.ktx)
    implementation (libs.material.v190)
    implementation (libs.viewpager2)
    implementation (libs.firebase.storage.v2010)
    implementation (libs.google.firebase.analytics)
    implementation (libs.firebase.messaging)
    implementation (libs.swiperefreshlayout)
    implementation (libs.photoview)
    implementation (libs.firebase.messaging.v2331)
    implementation (libs.google.firebase.auth)
    implementation (libs.libphonenumber)
    implementation (libs.media3.exoplayer)
    implementation (libs.media3.ui)
    implementation (libs.glide)
    // Retrofit dependencies
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.code.gson:gson:2.8.8")
    annotationProcessor (libs.compiler)
}