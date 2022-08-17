package com.siemens.mindsphere.tenant;

public interface TenantService {

    String tenantFromClientId(Object clientId);

    String bpnFromTenantName(String tenantName);
}
