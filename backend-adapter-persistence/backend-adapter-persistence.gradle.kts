plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    api(project(":backend-port-persistence"))

    implementation("org.springframework.boot:spring-boot-starter")
}