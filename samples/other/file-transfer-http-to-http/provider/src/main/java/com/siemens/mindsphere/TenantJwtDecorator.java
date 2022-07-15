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