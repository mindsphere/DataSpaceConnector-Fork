plugins {
    `java-library`
    id("application")
}

val jupiterVersion: String by project
val rsApi: String by project
val openTelemetryVersion: String by project
val nimbusVersion: String by project
val jerseyVersion: String by project

dependencies {
    implementation(project(":core"))
    implementation(project(":core:common:boot"))
    implementation(project(":spi:common:core-spi"))
    implementation(project(":common:util"))

    implementation(project(":samples:other:file-transfer-http-to-http:api"))
}
