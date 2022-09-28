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
val jupiterVersion: String by project
val assertj: String by project

plugins {
    `java-library`
    `java-test-fixtures`
}


dependencies {
    api(project(":spi:common:core-spi"))

    testFixturesImplementation("org.junit.jupiter:junit-jupiter-api:${jupiterVersion}")
    testFixturesImplementation("org.junit.jupiter:junit-jupiter-params:${jupiterVersion}")
    testFixturesImplementation("org.assertj:assertj-core:${assertj}")
}

publishing {
    publications {
        create<MavenPublication>("policy-spi") {
            artifactId = "policy-spi"
            from(components["java"])
        }
    }
}
