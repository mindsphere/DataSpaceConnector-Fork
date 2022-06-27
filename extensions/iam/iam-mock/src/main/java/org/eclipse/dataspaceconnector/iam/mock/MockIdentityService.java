/*
 *  Copyright (c) 2020, 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *       Fraunhofer Institute for Software and Systems Engineering - Improvements
 *
 */

package org.eclipse.dataspaceconnector.iam.mock;

import org.eclipse.dataspaceconnector.spi.iam.ClaimToken;
import org.eclipse.dataspaceconnector.spi.iam.IdentityService;
import org.eclipse.dataspaceconnector.spi.iam.TokenRepresentation;
import org.eclipse.dataspaceconnector.spi.result.Result;

import java.time.Clock;
import java.util.Map;

public class MockIdentityService implements IdentityService {
    private final String region;
    private final Clock clock;

    public MockIdentityService(Clock clock, String region) {
        this.clock = clock;
        this.region = region;
    }

    @Override
    public Result<TokenRepresentation> obtainClientCredentials(String scope, String tenant) {
        TokenRepresentation tokenRepresentation = TokenRepresentation.Builder.newInstance()
                .token("mock-" + region)
                .expiresIn(clock.instant().plusSeconds(10_0000).toEpochMilli())
                .additional(Map.of("ten", tenant))
                .build();
        return Result.success(tokenRepresentation);
    }

    @Override
    public Result<ClaimToken> verifyJwtToken(TokenRepresentation tokenRepresentation) {
        switch (tokenRepresentation.getToken()) {
            case "mock-eu":
                return Result.success(ClaimToken.Builder.newInstance().claim("region", "eu").build());
            case "mock-us":
                return Result.success(ClaimToken.Builder.newInstance().claim("region", "us").build());
            case "mock-an":
                return Result.success(ClaimToken.Builder.newInstance().claim("region", "an").build());
            default:
                return Result.failure("Unknown test token format");
        }
    }
}
