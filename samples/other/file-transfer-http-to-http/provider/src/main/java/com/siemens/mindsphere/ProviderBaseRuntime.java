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

package com.siemens.mindsphere;

import org.eclipse.dataspaceconnector.boot.system.DefaultServiceExtensionContext;
import org.eclipse.dataspaceconnector.boot.system.runtime.BaseRuntime;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.ConfigurationExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.telemetry.Telemetry;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProviderBaseRuntime extends BaseRuntime {

    private static class SiemensServiceExtensionContext extends DefaultServiceExtensionContext {
        SiemensServiceExtensionContext(TypeManager typeManager, Monitor monitor, Telemetry telemetry, List<ConfigurationExtension> configurationExtensions) {
            super(typeManager, monitor, telemetry, configurationExtensions);
        }

        @Override
        public <T> void registerService(Class<T> type, T service) {
            if (!super.hasService(type)) {
                super.registerService(type, service);
            }
        }
    }

    /**
     * The {@code main} method must be re-implemented, otherwise {@link BaseRuntime#main(String[])} would be called, which would
     * instantiate the {@code BaseRuntime}.
     */
    public static void main(String[] args) {
        new ProviderBaseRuntime().boot();
    }

    @Override
    protected @NotNull ServiceExtensionContext createContext(TypeManager typeManager, Monitor monitor, Telemetry telemetry) {
        return new SiemensServiceExtensionContext(typeManager, monitor, telemetry, loadConfigurationExtensions());
    }
}
