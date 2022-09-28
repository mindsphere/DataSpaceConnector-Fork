/*
 *  Copyright (c) 2022 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */


package org.eclipse.dataspaceconnector.iam.oauth2.core.rule;

import org.eclipse.dataspaceconnector.spi.iam.ClaimToken;
import org.eclipse.dataspaceconnector.spi.jwt.TokenValidationRule;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static org.eclipse.dataspaceconnector.spi.jwt.JwtRegisteredClaimNames.AUDIENCE;

/**
 * Token validation rule that checks if the "audience" of token contains the expected audience
 */
public class Oauth2AudienceValidationRule implements TokenValidationRule {

    private final String endpointAudience;

    public Oauth2AudienceValidationRule(String endpointAudience) {
        this.endpointAudience = endpointAudience;
    }

    @Override
    public Result<Void> checkRule(@NotNull ClaimToken toVerify, @Nullable Map<String, Object> additional) {
        var audiences = toVerify.getListClaim(AUDIENCE);
        if (audiences.isEmpty()) {
            return Result.failure("Required audience (aud) claim is missing in token");
        } else if (!audiences.contains(endpointAudience)) {
            return Result.failure("Token audience (aud) claim did not contain connector audience: " + endpointAudience);
        }

        return Result.success();
    }
}
