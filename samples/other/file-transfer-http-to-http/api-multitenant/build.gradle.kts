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
    implementation(project(":core:control-plane:control-plane-core"))

    implementation("org.glassfish.jersey.core:jersey-server:${jerseyVersion}")
    implementation("org.glassfish.jersey.containers:jersey-container-servlet-core:${jerseyVersion}")
    implementation("org.glassfish.jersey.core:jersey-common:${jerseyVersion}")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:${jerseyVersion}")
    implementation("org.glassfish.jersey.media:jersey-media-multipart:${jerseyVersion}")

    implementation(project(":core"))
    implementation(project(":core:common:boot"))
    implementation(project(":spi:common:core-spi"))
    implementation(project(":common:util"))

    implementation(project(":data-protocols:ids:ids-jsonld-serdes-lib"))

    implementation(project(":extensions:common:api:observability"))

    implementation(project(":extensions:common:configuration:filesystem-configuration"))

    implementation(project(":extensions:common:iam:oauth2:daps"))
    implementation(project(":extensions:common:iam:oauth2:oauth2-core"))
    implementation(project(":extensions:common:vault:hashicorp-vault"))

    implementation("com.nimbusds:nimbus-jose-jwt:${nimbusVersion}")

    implementation(project(":extensions:common:http"))

    implementation(project(":extensions:common:auth:auth-tokenbased"))
    implementation(project(":extensions:control-plane:api:data-management"))

    implementation(project(":data-protocols:ids")) {
        exclude("org.eclipse.dataspaceconnector","ids-token-validation")
    }

    implementation(project(":extensions:data-plane:data-plane-api"))
    implementation(project(":extensions:data-plane-selector:selector-client"))
    implementation(project(":core:data-plane-selector:data-plane-selector-core"))
    implementation(project(":core:data-plane:data-plane-framework"))
    implementation(project(":extensions:data-plane:data-plane-http"))

    implementation(project(":spi:common:core-spi"))

    implementation("io.opentelemetry:opentelemetry-extension-annotations:${openTelemetryVersion}")

    implementation(project(":spi:data-plane"))

    implementation(project(":samples:other:file-transfer-http-to-http:api"))

    api("jakarta.ws.rs:jakarta.ws.rs-api:${rsApi}")
}
