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
    implementation(project(":backend-common"))
    implementation(project(":backend-application"))
    implementation(project(":backend-adapter-web"))
    implementation(project(":backend-adapter-infra"))
    implementation(project(":backend-port-infra"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotest.spring)
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
