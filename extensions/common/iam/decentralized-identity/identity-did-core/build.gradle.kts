plugins {
    `java-library`
    id("io.swagger.core.v3.swagger-gradle-plugin")
}

val rsApi: String by project
val okHttpVersion: String by project

dependencies {
    api(project(":spi:common:identity-did-spi"))
    implementation(project(":extensions:common:iam:decentralized-identity:identity-did-crypto"))

    implementation("jakarta.ws.rs:jakarta.ws.rs-api:${rsApi}")
    implementation("com.squareup.okhttp3:okhttp:${okHttpVersion}")

    testImplementation(testFixtures(project(":extensions:common:iam:decentralized-identity:identity-common-test")))
    testImplementation(project(":extensions:common:junit"))
}

publishing {
    publications {
        create<MavenPublication>("identity-did-core") {
            artifactId = "identity-did-core"
            from(components["java"])
        }
    }
}
