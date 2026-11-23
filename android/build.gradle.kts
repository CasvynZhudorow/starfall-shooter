plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.shooter"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.shooter"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/*"
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.core.ktx)
    implementation(libs.constraintlayout)

    // LibGDX Android natives
    implementation(libs.libgdx.platform) {
        artifact {
            name = "gdx-platform"
            classifier = "natives-arm64-v8a"
        }
    }
    implementation(libs.libgdx.platform) {
        artifact {
            name = "gdx-platform"
            classifier = "natives-armeabi-v7a"
        }
    }
    implementation(libs.libgdx.platform) {
        artifact {
            name = "gdx-platform"
            classifier = "natives-x86"
        }
    }
    implementation(libs.libgdx.platform) {
        artifact {
            name = "gdx-platform"
            classifier = "natives-x86_64"
        }
    }
    implementation(libs.libgdx.box2d.platform) {
        artifact {
            name = "gdx-box2d-platform"
            classifier = "natives-arm64-v8a"
        }
    }
    implementation(libs.libgdx.box2d.platform) {
        artifact {
            name = "gdx-box2d-platform"
            classifier = "natives-armeabi-v7a"
        }
    }
    implementation(libs.libgdx.box2d.platform) {
        artifact {
            name = "gdx-box2d-platform"
            classifier = "natives-x86"
        }
    }
    implementation(libs.libgdx.box2d.platform) {
        artifact {
            name = "gdx-box2d-platform"
            classifier = "natives-x86_64"
        }
    }
    implementation(libs.libgdx.freetype.platform) {
        artifact {
            name = "gdx-freetype-platform"
            classifier = "natives-arm64-v8a"
        }
    }
    implementation(libs.libgdx.freetype.platform) {
        artifact {
            name = "gdx-freetype-platform"
            classifier = "natives-armeabi-v7a"
        }
    }
    implementation(libs.libgdx.freetype.platform) {
        artifact {
            name = "gdx-freetype-platform"
            classifier = "natives-x86"
        }
    }
    implementation(libs.libgdx.freetype.platform) {
        artifact {
            name = "gdx-freetype-platform"
            classifier = "natives-x86_64"
        }
    }

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.truth)
}