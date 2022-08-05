/*
 *  Copyright (c) 2021 - 2022 Daimler TSS GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Siemens - Initial API and Implementation
 *
 */

package com.siemens.mindsphere;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import org.eclipse.dataspaceconnector.common.token.JwtDecorator;

public class TenantJwtDecorator implements JwtDecorator {
    @Override
    public void decorate(JWSHeader.Builder header, JWTClaimsSet.Builder claimsSet) {
        claimsSet.claim("tenant", "my header tenant");
    }
}