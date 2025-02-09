plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
    application
}

application {
    mainClass.set("com.mono.backend.BackendApplicationKt")
}

dependencies {
    implementation(project(":backend-application"))
    implementation(project(":backend-adapter-web"))
    implementation(project(":backend-adapter-persistence"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
}

tasks.getByName<Jar>("jar") {
    enabled = false
}