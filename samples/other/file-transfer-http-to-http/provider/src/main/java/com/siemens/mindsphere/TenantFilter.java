package com.siemens.mindsphere;

import com.siemens.mindsphere.tenant.TenantService;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;

import java.io.IOException;

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
            var ten = headers.get("ten").stream().findFirst();

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
