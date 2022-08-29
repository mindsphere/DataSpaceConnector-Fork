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

package com.siemens.mindsphere.oauth;

import com.siemens.mindsphere.tenant.TenantService;
import org.eclipse.dataspaceconnector.common.token.JwtDecorator;
import org.eclipse.dataspaceconnector.common.token.TokenGenerationService;
import org.eclipse.dataspaceconnector.common.token.TokenGenerationServiceImpl;
import org.eclipse.dataspaceconnector.spi.iam.TokenRepresentation;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.jetbrains.annotations.NotNull;

public class SiemensTokenGenerationServiceImpl implements TokenGenerationService {

    private final TenantService tenantService;

    public SiemensTokenGenerationServiceImpl(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Override
    public Result<TokenRepresentation> generate(@NotNull JwtDecorator... decorators) {
        return new TokenGenerationServiceImpl(tenantService.getCurrentTenantPrivateKey()).generate(decorators);
    }
}
