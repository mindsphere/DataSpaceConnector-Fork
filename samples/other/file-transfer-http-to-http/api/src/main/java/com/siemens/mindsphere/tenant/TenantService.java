package com.siemens.mindsphere.tenant;

public interface TenantService {

    ThreadLocal<String> TLS_TENANT = new InheritableThreadLocal<>();

    String TENANT_PROPERTY = "ten";

    String tenantFromClientId(Object clientId);

    String bpnFromTenantName(String tenantName);
}
