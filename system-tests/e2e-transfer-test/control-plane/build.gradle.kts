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
}

dependencies {
    implementation(project(":core:control-plane:control-plane-core"))
    implementation(project(":data-protocols:ids"))
    implementation(project(":common:token-generation-lib"))
    implementation(project(":extensions:common:vault:filesystem-vault"))
    implementation(project(":extensions:common:http"))
    implementation(project(":extensions:common:iam:iam-mock"))
    implementation(project(":extensions:control-plane:api:data-management"))
    implementation(project(":extensions:control-plane:data-plane-transfer:data-plane-transfer-client"))
    implementation(project(":extensions:control-plane:data-plane-transfer:data-plane-transfer-sync"))

    implementation(project(":core:data-plane-selector:data-plane-selector-core"))
    implementation(project(":extensions:data-plane-selector:selector-api"))
    implementation(project(":extensions:data-plane-selector:selector-client"))

    implementation(project(":extensions:control-plane:provision:http-provisioner"))
    implementation(project(":extensions:control-plane:http-receiver"))
}
