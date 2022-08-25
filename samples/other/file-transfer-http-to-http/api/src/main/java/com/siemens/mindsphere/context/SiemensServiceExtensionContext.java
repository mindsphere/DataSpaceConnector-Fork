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

package com.siemens.mindsphere.context;

import org.eclipse.dataspaceconnector.boot.system.DefaultServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.ConfigurationExtension;
import org.eclipse.dataspaceconnector.spi.telemetry.Telemetry;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;

import java.util.List;

public class SiemensServiceExtensionContext extends DefaultServiceExtensionContext {
    public SiemensServiceExtensionContext(TypeManager typeManager, Monitor monitor, Telemetry telemetry, List<ConfigurationExtension> configurationExtensions) {
        super(typeManager, monitor, telemetry, configurationExtensions);
    }

    @Override
    public <T> void registerService(Class<T> type, T service) {
        if (!super.hasService(type) || type.getName().startsWith("Siemens")) {
            super.registerService(type, service);
        }
    }
}
