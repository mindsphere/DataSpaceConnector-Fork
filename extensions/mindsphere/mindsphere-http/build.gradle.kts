plugins {
    `java-library`
}

dependencies {
    api(project(":spi"))
    api(project(":extensions:inline-data-transfer:inline-data-transfer-spi"))

    testImplementation(testFixtures(project(":common:util")))
}

publishing {
    publications {
        create<MavenPublication>("mindsphere-http") {
            artifactId = "mindsphere-http"
            from(components["java"])
        }
    }
}
