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
 *       Fraunhofer Institute for Software and Systems Engineering - added dependencies
 *       ZF Friedrichshafen AG - add dependency
 *
 */

plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

val jupiterVersion: String by project
val rsApi: String by project

dependencies {
    implementation(project(":core"))

    implementation(project(":extensions:api:observability"))

    implementation(project(":extensions:filesystem:vault-fs"))
    implementation(project(":extensions:filesystem:configuration-fs"))

    implementation(project(":extensions:iam:iam-mock"))

    implementation(project(":extensions:api:auth-tokenbased"))
    implementation(project(":extensions:api:data-management"))

    implementation(project(":data-protocols:ids")) {
        exclude("org.eclipse.dataspaceconnector","ids-token-validation")
    }
//    implementation(project(":data-protocols:ids:ids-api-multipart-endpoint-v1"))

//    implementation(project(":extensions:data-plane-transfer:data-plane-transfer-client"))
//    implementation(project(":extensions:data-plane-transfer:data-plane-transfer-sync"))
    implementation(project(":extensions:data-plane:data-plane-api"))
    implementation(project(":extensions:data-plane-selector:selector-client"))
    implementation(project(":extensions:data-plane-selector:selector-core"))
    implementation(project(":extensions:data-plane-selector:selector-store"))
    implementation(project(":extensions:data-plane:data-plane-framework"))
    implementation(project(":extensions:data-plane:data-plane-http"))

    implementation(project(":samples:other:file-transfer-http-to-http:api"))
}

application {
    mainClass.set("org.eclipse.dataspaceconnector.boot.system.runtime.BaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("**/pom.properties", "**/pom.xm")
    mergeServiceFiles()
    archiveFileName.set("consumer.jar")
}
