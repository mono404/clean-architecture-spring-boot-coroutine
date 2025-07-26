dependencies {
    implementation(project(":backend-domain"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    // kotlin
    api("org.jetbrains.kotlin:kotlin-reflect")

    // Log
    api("org.slf4j:slf4j-api")
}
