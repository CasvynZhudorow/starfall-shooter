plugins {
    id("org.jetbrains.kotlin.jvm")
    id("application")
}

group = "com.example.shooter"
version = "1.0.0"

application {
    mainClass.set("com.example.shooter.DesktopLauncher")
}

dependencies {
    implementation(project(":core"))
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
}

kotlin {
    jvmToolchain(21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaExec> {
    jvmArgs("-Xmx1G", "-XX:+UseG1GC")
}