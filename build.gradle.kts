import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    id("org.springframework.boot")
    id("io.spring.dependency-management")
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

// 루트 프로젝트는 bootJar 태스크를 비활성화
tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

subprojects {
    group = "com.mono.backend"

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    repositories {
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }

    extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
        jvmToolchain(17)
    }

    tasks.withType<JavaCompile> {
        options.encoding = "utf-8"
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-Xjsr305=strict")
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            javaParameters.set(true)
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        // 병렬 실행
        systemProperty("kotest.framework.parallelism", Runtime.getRuntime().availableProcessors())
    }

    // backend-bootstrap 모듈을 제외한 모든 모듈에서 bootJar 태스크 비활성화
    if (project.name != "backend-bootstrap") {
        tasks.bootJar {
            enabled = false
        }
        tasks.jar {
            enabled = true
        }
    }
}
