plugins {
    `java-library`
}

val rsApi: String by project

dependencies {
    api(project(":spi"))
    api(project(":core"))

    implementation(project(":common:util"))

    implementation(project(":extensions:mindsphere:mindsphere-http"))

    implementation(project(":extensions:sql:common-sql"))
    implementation(project(":extensions:sql:asset-index-sql"))
    implementation(project(":extensions:sql:contract-definition-store-sql"))
    implementation(project(":extensions:sql:policy-store-sql"))
    implementation(project(":extensions:transaction:transaction-local"))
    implementation("org.postgresql:postgresql:42.2.6")

    implementation("jakarta.ws.rs:jakarta.ws.rs-api:${rsApi}")
}
