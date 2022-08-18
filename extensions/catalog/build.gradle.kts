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

plugins {
    `java-library`
}

//This file serves as BOM for the federated catalog
dependencies {
    api(project(":extensions:catalog:federated-catalog-cache"))
    api(project(":spi:federated-catalog:federated-catalog-spi"))
}

publishing {
    publications {
        create<MavenPublication>("catalog") {
            artifactId = "catalog"
            from(components["java"])
        }
    }
}
