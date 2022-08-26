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

import java.security.PrivateKey;

public interface TenantService {

    ThreadLocal<String> TLS_TENANT = new InheritableThreadLocal<>();

    String TENANT_PROPERTY = "ten";

    String bpnFromTenantName(String tenantName);

    PrivateKey getCurrentTenantPrivateKey();

    byte[] getCurrentTenantEncodedClientCertificate();

    String getCurrentTenantClientId();
}
