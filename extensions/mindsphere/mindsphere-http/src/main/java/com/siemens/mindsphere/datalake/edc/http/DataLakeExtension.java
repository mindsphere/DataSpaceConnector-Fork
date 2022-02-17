package com.siemens.mindsphere.datalake.edc.http;

import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transfer.inline.DataOperatorRegistry;

public class DataLakeExtension implements ServiceExtension {
    @Inject
    private DataOperatorRegistry dataOperatorRegistry;

    @Override
    public void initialize(ServiceExtensionContext context) {
        Monitor monitor = context.getMonitor();
        // create Data Lake client
        final DataLakeClient dataLakeClient = new DataLakeClientImpl();
        // create Data Lake Reader
        final DataLakeReader dataLakeReader = new DataLakeReader(dataLakeClient, monitor);
        // register Data Lake Reader
        dataOperatorRegistry.registerReader(dataLakeReader);
    }

    @Override
    public String name() {
        return "MindSphere DataLake";
    }
}
