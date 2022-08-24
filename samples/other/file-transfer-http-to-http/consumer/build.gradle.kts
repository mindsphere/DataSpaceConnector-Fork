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

    implementation(project(":extensions:common:api:observability"))

    // implementation(project(":extensions:filesystem:vault-fs"))
    implementation(project(":extensions:common:vault:hashicorp-vault"))
    implementation(project(":extensions:common:configuration:filesystem-configuration"))

    //use like gradle dependencies -P localdevelopment
    if( project.hasProperty("localdevelopment")) {
        implementation(project(":extensions:common:iam:iam-mock"))
    } else {
        implementation(project(":extensions:common:iam:oauth2:daps"))
        implementation(project(":extensions:common:iam:oauth2:oauth2-core"))
    }

    implementation(project(":extensions:common:auth:auth-tokenbased"))
    implementation(project(":extensions:control-plane:api:data-management"))

    implementation(project(":data-protocols:ids")) {
        exclude("org.eclipse.dataspaceconnector","ids-token-validation")
    }

    implementation(project(":extensions:data-plane:data-plane-api"))
    implementation(project(":extensions:data-plane-selector:selector-client"))
    implementation(project(":core:data-plane-selector:data-plane-selector-core"))
    implementation(project(":core:data-plane:data-plane-framework"))
    implementation(project(":extensions:data-plane:data-plane-http"))

    // implementation(project(":samples:other:file-transfer-http-to-http:api"))
}

application {
    mainClass.set("com.siemens.mindsphere.ConsumerBaseRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("**/pom.properties", "**/pom.xm")
    mergeServiceFiles()
    archiveFileName.set("consumer.jar")
}
