dependencies {
    api(project(":backend-domain"))

    implementation("org.springframework:spring-web")
    implementation("org.springframework.data:spring-data-commons")
    api("software.amazon.awssdk:s3:2.31.63")
}
