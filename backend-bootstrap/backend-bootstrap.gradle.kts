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
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
