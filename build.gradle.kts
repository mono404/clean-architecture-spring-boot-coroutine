import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

group = "com.mono"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

subprojects {
    group = "com.mono.backend"

    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "utf-8"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
            javaParameters = true
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
