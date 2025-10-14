plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin1kept)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.tech.sid"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.tech.sid"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "2.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src/main/assets")
            }
        }
    }
}
/*  ******
LOG PRINT DATA

*API_RESPONSE  =>*****  API***** RESPONSE *****
*DEBUG_LINE_LOG ===> Constants.logPrint("sdfsadfsdfasd")
*DEBUG_LOG  ===>  Constants.logPrint("sdfsadfsdfasd")
*BEARER_TOKEN
*******  */

/* ****** Firebase Project
* Project Name : Wedding App
* Associated Id : vikram@techwinlabs.com
****** */

dependencies {
    implementation(libs.imagepicker)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.databinding.runtime)
    implementation(libs.dagger)
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.play.services.location)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.glide)
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)
    implementation(libs.gson)
    kapt(libs.dagger.compiler)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.lottie)
    implementation(libs.converter.gson)
//    implementation (libs.ccp)
    //mpChart
    implementation  (libs.mpandroidchart)
    implementation  ("com.google.android.material:material:1.13.0")
    //svg images
    implementation  (libs.androidsvg)
    implementation(libs.billing)
    implementation(libs.play.services.auth)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)


}