/*
 *  Copyright (c) 2020 - 2022 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation

 *
 */

val rsApi: String by project
val restAssured: String by project

plugins {
    `java-library`
    id("io.swagger.core.v3.swagger-gradle-plugin")
}

dependencies {
    implementation(project(":spi:common:auth-spi"))
    implementation(project(":spi:common:catalog-spi"))
    implementation(project(":extensions:common:api:api-core"))
    implementation(project(":extensions:control-plane:api:data-management:api-configuration"))

    implementation("jakarta.ws.rs:jakarta.ws.rs-api:${rsApi}")

    testImplementation(project(":extensions:common:http"))
    testImplementation(project(":extensions:common:iam:iam-mock"))

    testImplementation(project(":extensions:common:junit"))
    testImplementation("io.rest-assured:rest-assured:${restAssured}")
}

publishing {
    publications {
        create<MavenPublication>("catalog-api") {
            artifactId = "catalog-api"
            from(components["java"])
        }
    }
}
