dependencies {
    api(project(":backend-domain"))
    implementation("org.springframework:spring-web:6.2.7")
    api("software.amazon.awssdk:s3:2.31.63")
}