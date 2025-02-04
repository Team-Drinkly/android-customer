import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}


val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}
val kakaoNativeKey = properties.getProperty("kakao_app_key")

android {
    namespace = "com.project.drinkly"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.project.drinkly"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "KAKAO_APP_KEY", "\"${properties["kakao_key"]}\"")
//        buildConfigField("String", "SERVER_URL", properties.getProperty("SERVER_URL"))

        manifestPlaceholders["kakao_native_key"] = kakaoNativeKey
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    dataBinding {
        enable = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // onboarding dot indicator
    implementation("com.tbuonomo:dotsindicator:5.1.0")

    // 카카오 로그인
    implementation("com.kakao.sdk:v2-user:2.20.6")
}