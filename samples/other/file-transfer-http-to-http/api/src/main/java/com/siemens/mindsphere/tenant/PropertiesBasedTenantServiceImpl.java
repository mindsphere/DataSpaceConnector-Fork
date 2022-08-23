package com.siemens.mindsphere.tenant;

import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PropertiesBasedTenantServiceImpl implements TenantService {

    private static final String CLIENT_ID_TO_TENANT = "tenants.clientid.to.tenant";
    private static final String TENANT_TO_BPN = "tenants.tenant.to.bpn";

    private final Map<String, String> clientToTenant;
    private final Map<String, String> tenantToBpn;

    public PropertiesBasedTenantServiceImpl(ServiceExtensionContext context) {
        var tenantMapping = context.getSetting(CLIENT_ID_TO_TENANT, "");
        var bpnMapping = context.getSetting(TENANT_TO_BPN, "");

        clientToTenant = extractMapFromString(tenantMapping);
        tenantToBpn = extractMapFromString(bpnMapping);

    }

    private Map<String, String> extractMapFromString(String tenantMapping) {
        final Map<String, String> ret = new HashMap<>();

        if (Objects.isNull(tenantMapping) || tenantMapping.isEmpty()) {
            return ret;
        }

        for (String keyValue : tenantMapping.split(",")) {
            int indexEqual = keyValue.indexOf('=');
            if (indexEqual < 1) {
                continue;
            }

            final String key = keyValue.substring(0, indexEqual);
            final String value = keyValue.substring(indexEqual + 1);
            ret.put(key, value);
        }

        return ret;
    }

    @Override
    public String tenantFromClientId(Object clientId) {
        if (Objects.isNull(clientId)) {
            return null;
        }

        return clientToTenant.get(clientId.toString());
    }

    @Override
    public String bpnFromTenantName(String tenantName) {
        if (Objects.isNull(tenantName)) {
            return null;
        }

        return tenantToBpn.get(tenantName);
    }
}
