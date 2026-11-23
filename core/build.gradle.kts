plugins {
    id("org.jetbrains.kotlin.jvm")
}

group = "com.example.shooter"
version = "1.0.0"

dependencies {
    implementation(libs.libgdx)
    implementation(libs.libgdx.box2d)
    implementation(libs.libgdx.freetype)
    implementation(libs.ashley)
    
    // Desktop-only dependencies for testing
    implementation(libs.libgdx.platform) {
        artifact {
            name = "gdx-platform"
            classifier = "natives-desktop"
        }
    }
    implementation(libs.libgdx.box2d.platform) {
        artifact {
            name = "gdx-box2d-platform"
            classifier = "natives-desktop"
        }
    }
    implementation(libs.libgdx.freetype.platform) {
        artifact {
            name = "gdx-freetype-platform"
            classifier = "natives-desktop"
        }
    }

    testImplementation(libs.junit)
}

kotlin {
    jvmToolchain(21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
    useJUnit()
    testLogging {
        events("passed", "skipped", "failed")
    }
}