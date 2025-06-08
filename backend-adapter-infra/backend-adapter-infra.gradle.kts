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
    runtimeOnly("io.asyncer:r2dbc-mysql:1.3.0")

    api("software.amazon.awssdk:s3:2.31.52")
//    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}