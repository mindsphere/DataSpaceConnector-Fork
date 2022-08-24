/*
 *  Copyright (c) 2020 - 2022 Microsoft Corporation
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

plugins {
    `java-library`
}

dependencies {
    api(project(":extensions:common:api:api-core"))
    api(project(":extensions:control-plane:api:data-management:api-configuration"))
    api(project(":extensions:control-plane:api:data-management:asset-api"))
    api(project(":extensions:control-plane:api:data-management:catalog-api"))
    api(project(":extensions:control-plane:api:data-management:contractagreement-api"))
    api(project(":extensions:control-plane:api:data-management:contractdefinition-api"))
    api(project(":extensions:control-plane:api:data-management:contractnegotiation-api"))
    api(project(":extensions:control-plane:api:data-management:policydefinition-api"))
    api(project(":extensions:control-plane:api:data-management:transferprocess-api"))
}

publishing {
    publications {
        create<MavenPublication>("data-management-api") {
            artifactId = "data-management-api"
            from(components["java"])
        }
    }
}
