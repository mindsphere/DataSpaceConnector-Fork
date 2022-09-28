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

val awaitility: String by project
val openTelemetryVersion: String by project

plugins {
    `java-library`
}

dependencies {
    api(project(":spi:control-plane:policy-spi"))
    api(project(":spi:control-plane:transfer-spi"))
    implementation(project(":core:common:state-machine"))
    implementation(project(":core:common:util"))
    implementation("io.opentelemetry:opentelemetry-extension-annotations:${openTelemetryVersion}")

    testImplementation(project(":extensions:common:junit"))
    testImplementation(project(":core:control-plane:control-plane-core"))
    testImplementation(testFixtures(project(":core:common:util")))
    testImplementation("org.awaitility:awaitility:${awaitility}")
}


publishing {
    publications {
        create<MavenPublication>("transfer") {
            artifactId = "transfer"
            from(components["java"])
        }
    }
}
