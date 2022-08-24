/*
 *  Copyright (c) 2022 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

plugins {
    `java-library`
    `maven-publish`
}

val awaitility: String by project
val cloudEvents: String by project
val httpMockServer: String by project
val failsafeVersion: String by project
val okHttpVersion: String by project

dependencies {
    implementation(project(":spi:common:core-spi"))

    implementation("com.squareup.okhttp3:okhttp:${okHttpVersion}")
    implementation("io.cloudevents:cloudevents-http-basic:${cloudEvents}")
    implementation("dev.failsafe:failsafe:${failsafeVersion}")

    testImplementation(testFixtures(project(":extensions:common:junit")))
    testImplementation("org.mock-server:mockserver-netty:${httpMockServer}:shaded")
    testImplementation("org.awaitility:awaitility:${awaitility}")
}

publishing {
    publications {
        create<MavenPublication>("cloudevents-http") {
            artifactId = "cloudevents-http"
            from(components["java"])
        }
    }
}
