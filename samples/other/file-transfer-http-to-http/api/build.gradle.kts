plugins {
    `java-library`
    id("application")
}

val rsApi: String by project

dependencies {
    api(project(":spi"))
    api(project(":core:transfer"))
    api(project(":spi:common:core-spi"))

    implementation(project(":common:util"))

    api("jakarta.ws.rs:jakarta.ws.rs-api:${rsApi}")
}