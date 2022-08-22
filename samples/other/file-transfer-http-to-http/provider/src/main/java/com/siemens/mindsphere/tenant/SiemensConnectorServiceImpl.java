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
