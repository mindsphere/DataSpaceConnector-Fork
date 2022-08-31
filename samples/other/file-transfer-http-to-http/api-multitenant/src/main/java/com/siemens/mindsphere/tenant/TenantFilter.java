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

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;

import java.io.IOException;
import java.util.stream.Collectors;

@Provider
public class TenantFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private final Monitor monitor;

    public TenantFilter(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        try {
            var headers = requestContext.getHeaders();
            monitor.debug(() -> "Headers --> " + headers.keySet().stream().map(key -> key + "=" + headers.get(key)).collect(Collectors.joining("\t")));

            var tenList = headers.get("ten");
            if (tenList == null)  {
                monitor.warning("There is no tenant set in the header");
                return;
            }

            var ten = tenList.stream().findFirst();

            if (ten.isPresent()) {
                TenantService.TLS_TENANT.set(ten.get());
                monitor.debug("TenantFilter called for " + ten);
            } else {
                monitor.warning("There is no tenant set in the header");
            }
        } catch (Exception e) {
            monitor.severe("Failed to add filter for tenant", e);
        }
    }


    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        TenantService.TLS_TENANT.remove();
        monitor.debug("TenantFilter cleaning up");
    }
}
