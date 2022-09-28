/*
 *  Copyright (c) 2020, 2021 Microsoft Corporation
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

val failsafeVersion: String by project
val httpMockServer: String by project
val okHttpVersion: String by project
val restAssured: String by project
val rsApi: String by project

plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    api(project(":spi:data-plane:data-plane-spi"))
    implementation(project(":core:common:util"))
    implementation("com.squareup.okhttp3:okhttp:${okHttpVersion}")
    implementation("dev.failsafe:failsafe:${failsafeVersion}")

    testImplementation(project(":extensions:common:junit"))
    testImplementation(testFixtures(project(":core:common:util")))
    testImplementation("io.rest-assured:rest-assured:${restAssured}")
    testImplementation("org.mock-server:mockserver-netty:${httpMockServer}:shaded")
    testFixturesImplementation("com.squareup.okhttp3:okhttp:${okHttpVersion}")
}

publishing {
    publications {
        create<MavenPublication>("data-plane-http") {
            artifactId = "data-plane-http"
            from(components["java"])
        }
    }
}
