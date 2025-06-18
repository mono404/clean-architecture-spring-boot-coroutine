plugins {
    kotlin("kapt")
}

dependencies {
    val coroutineVersion: String by project
    val jacksonVersion: String by project
    implementation(project(":backend-port-infra"))


    // kotlin
    api("org.jetbrains.kotlin:kotlin-reflect")

    // coroutine
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutineVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$coroutineVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")

    // Log
    api("org.slf4j:slf4j-api:1.7.36")
    api("ch.qos.logback:logback-classic:1.2.11")
    api("org.codehaus.janino:janino:3.0.6")

    // Jackson
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    api("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    api("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    api("org.springframework.boot:spring-boot-starter-security:3.4.5")

    api("io.mockk:mockk:1.14.2")
    api("org.springframework.boot:spring-boot-starter-test:3.4.5")
    api("org.junit.platform:junit-platform-launcher:1.12.2")

    api("org.springframework:spring-web:6.2.7")
}
