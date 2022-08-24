/*
 *  Copyright (c) 2020-2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

val mockitoVersion: String by project

plugins {
    `java-library`
    id("io.swagger.core.v3.swagger-gradle-plugin")
}

val rsApi: String by project
val okHttpVersion: String by project


dependencies {
    api(project(":spi:common:core-spi"))
    api(project(":extensions:common:http"))
    api(project(":extensions:common:configuration:filesystem-configuration"))
    api(project(":spi:data-plane-selector:data-plane-selector-spi"))
    implementation(project(":common:util"))
    implementation("jakarta.ws.rs:jakarta.ws.rs-api:${rsApi}")
    implementation(project(":extensions:common:api:api-core")) //for the exception mapper


    testImplementation(project(":core:data-plane-selector:data-plane-selector-core"))

    testImplementation("com.squareup.okhttp3:okhttp:${okHttpVersion}")
    testImplementation(project(":extensions:common:http"))

    testImplementation(project(":extensions:common:junit"))

}


publishing {
    publications {
        create<MavenPublication>("data-plane-selector-api") {
            artifactId = "data-plane-selector-api"
            from(components["java"])
        }
    }
}
