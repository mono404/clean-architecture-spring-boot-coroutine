plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

// 이 모듈은 독립 실행 가능한 JAR이 아니므로 bootJar 태스크를 비활성화
tasks.bootJar {
    enabled = false
}

// 일반 jar 태스크는 활성화
tasks.jar {
    enabled = true
}

dependencies {
    implementation(project(":backend-port-web"))
    implementation(project(":backend-common"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json")

    testImplementation(libs.kotest.assertions)
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.spring)
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}
