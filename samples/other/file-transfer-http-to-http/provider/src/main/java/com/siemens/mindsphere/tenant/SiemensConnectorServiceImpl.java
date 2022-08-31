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
 *
 */

package com.siemens.mindsphere.tenant;

import org.eclipse.dataspaceconnector.ids.core.service.ConnectorServiceImpl;
import org.eclipse.dataspaceconnector.ids.core.service.ConnectorServiceSettings;
import org.eclipse.dataspaceconnector.ids.spi.service.CatalogService;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.jetbrains.annotations.NotNull;

public class SiemensConnectorServiceImpl extends ConnectorServiceImpl {

    public SiemensConnectorServiceImpl(@NotNull Monitor monitor, @NotNull ConnectorServiceSettings connectorServiceSettings, @NotNull CatalogService dataCatalogService) {
        super(monitor, connectorServiceSettings, dataCatalogService);
    }
}
