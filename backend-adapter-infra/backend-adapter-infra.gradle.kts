plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    application
}

dependencies {
    implementation(project(":backend-port-infra"))
    implementation(project(":backend-common"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("io.r2dbc:r2dbc-proxy")
    runtimeOnly("io.asyncer:r2dbc-mysql:1.3.0")

    // Firebase Admin SDK
    implementation("com.google.firebase:firebase-admin:9.5.0")

    // kotlinx
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
