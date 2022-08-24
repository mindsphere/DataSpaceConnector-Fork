/*
 * Copyright (c) 2022 ZF Friedrichshafen AG
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *   ZF Friedrichshafen AG - Initial API and Implementation
 */

val awaitility: String by project
val jerseyVersion: String by project
val restAssured: String by project
val rsApi: String by project

plugins {
    `java-library`
    id("io.swagger.core.v3.swagger-gradle-plugin")
}

dependencies {
    api(project(":spi:control-plane:control-plane-spi"))
    api(project(":spi:common:transaction-spi"))
    implementation(project(":extensions:common:api:api-core"))
    implementation(project(":extensions:control-plane:api:data-management:api-configuration"))

    implementation("jakarta.ws.rs:jakarta.ws.rs-api:${rsApi}")

    testImplementation(project(":common:util"))
    testImplementation(project(":core:control-plane:control-plane-core"))
    testImplementation(project(":extensions:common:http"))
    testImplementation(project(":extensions:common:junit"))

    testImplementation("io.rest-assured:rest-assured:${restAssured}")
    testImplementation("org.awaitility:awaitility:${awaitility}")
}

publishing {
    publications {
        create<MavenPublication>("contractnegotiation-api") {
            artifactId = "contractnegotiation-api"
            from(components["java"])
        }
    }
}
