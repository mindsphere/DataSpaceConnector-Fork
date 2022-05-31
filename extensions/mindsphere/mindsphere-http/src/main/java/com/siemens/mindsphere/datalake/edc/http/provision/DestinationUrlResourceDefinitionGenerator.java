/*
 *  Copyright (c) 2021, 2022 Siemens AG
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

package com.siemens.mindsphere.datalake.edc.http.provision;

import com.siemens.mindsphere.datalake.edc.http.DataLakeSchema;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.transfer.provision.ConsumerResourceDefinitionGenerator;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataRequest;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.ResourceDefinition;
import org.jetbrains.annotations.Nullable;

import static java.util.UUID.randomUUID;

public class DestinationUrlResourceDefinitionGenerator implements ConsumerResourceDefinitionGenerator {
    public DestinationUrlResourceDefinitionGenerator(Monitor monitor) {
        this.monitor = monitor;
    }

    private static final String PATH = "path";

    private Monitor monitor;

    @Override
    public @Nullable ResourceDefinition generate(DataRequest dataRequest, Policy policy) {
        if (dataRequest.getDestinationType() == null) {
            return null;
        }

        final String dataDestinationType = dataRequest.getDataDestination().getType();
        if (!DataLakeSchema.TYPE.equals(dataDestinationType)) {
            return null;
        }

        monitor.info("Generating destination URL resource definition for dataRequest: " + dataRequest.getId());

        final String destinationPath = dataRequest.getDataDestination().getProperty(PATH);

        return DestinationUrlResourceDefinition.Builder.newInstance()
                .id(randomUUID().toString())
                .path(destinationPath)
                .build();
    }

}
