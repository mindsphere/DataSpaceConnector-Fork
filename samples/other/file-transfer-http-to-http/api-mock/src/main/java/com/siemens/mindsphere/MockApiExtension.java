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

import com.siemens.mindsphere.tenant.MockTenantServiceImpl;
import com.siemens.mindsphere.tenant.TenantService;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

@Provides({ TenantService.class })
public class MockApiExtension implements ServiceExtension {

    @Override
    public void initialize(ServiceExtensionContext context) {
        var tenantService = new MockTenantServiceImpl();
        context.registerService(TenantService.class, tenantService);
    }
}
