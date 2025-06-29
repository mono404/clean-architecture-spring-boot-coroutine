dependencies {
    api(project(":backend-domain"))
    api(project(":backend-common"))

    implementation("org.springframework.security:spring-security-core")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
}
