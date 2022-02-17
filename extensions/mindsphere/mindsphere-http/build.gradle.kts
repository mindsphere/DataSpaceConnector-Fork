plugins {
    `java-library`
}

dependencies {
    api(project(":spi"))

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
