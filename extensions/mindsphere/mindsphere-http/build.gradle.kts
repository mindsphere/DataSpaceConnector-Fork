plugins {
    `java-library`
}

val failsafeVersion: String by project
val okHttpVersion: String by project
val jacksonVersion: String by project
val mockitoVersion: String by project
val openTelemetryVersion: String by project

dependencies {
    api(project(":spi"))
    implementation("dev.failsafe:failsafe:${failsafeVersion}")

    implementation("com.squareup.okhttp3:okhttp:${okHttpVersion}")
    implementation("com.fasterxml.jackson.core:jackson-core:${jacksonVersion}")
    implementation("com.fasterxml.jackson.core:jackson-annotations:${jacksonVersion}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}")

    testImplementation("com.squareup.okhttp3:mockwebserver:${okHttpVersion}")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
    testImplementation(testFixtures(project(":common:util")))

    implementation(project(":extensions:control-plane:data-plane-transfer:data-plane-transfer-client"))
    implementation(project(":extensions:data-plane-selector:selector-client"))
    implementation(project(":core:data-plane-selector:data-plane-selector-core"))
    implementation(project(":core:data-plane:data-plane-framework"))
    implementation(project(":extensions:data-plane:data-plane-http"))

    implementation("io.opentelemetry:opentelemetry-extension-annotations:${openTelemetryVersion}")

    implementation(project(":spi:data-plane"))
}

publishing {
    publications {
        create<MavenPublication>("mindsphere-http") {
            artifactId = "mindsphere-http"
            from(components["java"])
        }
    }
}
