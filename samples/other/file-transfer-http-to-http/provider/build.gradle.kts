plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

val jupiterVersion: String by project
val rsApi: String by project
val openTelemetryVersion: String by project
val nimbusVersion: String by project
val jerseyVersion: String by project

dependencies {
    implementation(project(":core"))
    implementation(project(":core:defaults"))

    implementation("org.glassfish.jersey.core:jersey-server:${jerseyVersion}")
    implementation("org.glassfish.jersey.containers:jersey-container-servlet-core:${jerseyVersion}")
    implementation("org.glassfish.jersey.core:jersey-common:${jerseyVersion}")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:${jerseyVersion}")
    implementation("org.glassfish.jersey.media:jersey-media-multipart:${jerseyVersion}")

    implementation(project(":data-protocols:ids:ids-jsonld-serdes-lib"))

    implementation(project(":extensions:api:observability"))

    implementation(project(":extensions:filesystem:vault-fs"))
    implementation(project(":extensions:filesystem:configuration-fs"))

    //use like gradle dependencies -P localdevelopment
    if( project.hasProperty("localdevelopment")) {
        implementation(project(":extensions:iam:iam-mock"))
    } else {
        implementation(project(":extensions:iam:daps"))
        implementation(project(":extensions:iam:oauth2:oauth2-core"))
    }

    implementation("com.nimbusds:nimbus-jose-jwt:${nimbusVersion}")

    implementation(project(":extensions:iam:oauth2:oauth2-spi"))

    testImplementation(project(":extensions:iam:oauth2:oauth2-core"))

    implementation(project(":extensions:http"))

    implementation(project(":extensions:api:auth-tokenbased"))
    implementation(project(":extensions:api:data-management"))

    implementation(project(":data-protocols:ids")) {
        exclude("org.eclipse.dataspaceconnector","ids-token-validation")
    }

    implementation(project(":extensions:data-plane:data-plane-api"))
    implementation(project(":extensions:data-plane-selector:selector-client"))
    implementation(project(":extensions:data-plane-selector:selector-core"))
    implementation(project(":extensions:data-plane-selector:selector-store"))
    implementation(project(":extensions:data-plane:data-plane-framework"))
    implementation(project(":extensions:data-plane:data-plane-http"))

    implementation(project(":extensions:dataloading"))

    implementation("io.opentelemetry:opentelemetry-extension-annotations:${openTelemetryVersion}")

    implementation(project(":extensions:data-plane:data-plane-spi"))

    implementation(project(":extensions:mindsphere:mindsphere-http"))

    implementation(project(":samples:other:file-transfer-http-to-http:transfer-file"))

    implementation(project(":samples:other:file-transfer-http-to-http:api"))

    implementation("jakarta.ws.rs:jakarta.ws.rs-api:${rsApi}")
}

application {
    mainClass.set("com.siemens.mindsphere.ProviderBaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("**/pom.properties", "**/pom.xm")
    mergeServiceFiles()
    archiveFileName.set("provider.jar")
}
