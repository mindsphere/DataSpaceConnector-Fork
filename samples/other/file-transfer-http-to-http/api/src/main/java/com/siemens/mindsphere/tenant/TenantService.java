package com.siemens.mindsphere.tenant;

public interface TenantService {

    static final ThreadLocal<String> TLS_TENANT = new InheritableThreadLocal<>();

    String tenantFromClientId(Object clientId);

    String bpnFromTenantName(String tenantName);
}
