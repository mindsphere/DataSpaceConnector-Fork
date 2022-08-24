/*
 *  Copyright (c) 2022 Microsoft Corporation
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

val azureIdentityVersion: String by project
val azureResourceManagerVersion: String by project
val awaitility: String by project
val azureResourceManagerDataFactory: String by project
val bouncycastleVersion: String by project

plugins {
    `java-library`
}

dependencies {
    api(project(":spi:data-plane:data-plane-spi"))
    implementation(project(":extensions:common:azure:blob-core"))
    implementation(project(":common:util"))
    implementation("com.azure:azure-identity:${azureIdentityVersion}")
    implementation("com.azure.resourcemanager:azure-resourcemanager-datafactory:${azureResourceManagerDataFactory}")
    implementation("com.azure.resourcemanager:azure-resourcemanager-storage:${azureResourceManagerVersion}")
    implementation("com.azure.resourcemanager:azure-resourcemanager-keyvault:${azureResourceManagerVersion}")
    implementation("com.azure.resourcemanager:azure-resourcemanager:${azureResourceManagerVersion}")
    implementation("com.azure.resourcemanager:azure-resourcemanager-authorization:${azureResourceManagerVersion}")

    testImplementation(project(":extensions:common:configuration:filesystem-configuration"))
    testImplementation(project(":core:data-plane:data-plane-core"))
    testImplementation(project(":extensions:common:azure:azure-resource-manager"))
    testImplementation(testFixtures(project(":extensions:common:azure:azure-test")))
    testImplementation(testFixtures(project(":extensions:common:azure:blob-core")))

    testImplementation(project(":extensions:common:junit"))
    testImplementation("org.awaitility:awaitility:${awaitility}")
    testImplementation("org.bouncycastle:bcprov-jdk15on:${bouncycastleVersion}")
}

publishing {
    publications {
        create<MavenPublication>("data-plane-azure-data-factory") {
            artifactId = "data-plane-azure-data-factory"
            from(components["java"])
        }
    }
}
