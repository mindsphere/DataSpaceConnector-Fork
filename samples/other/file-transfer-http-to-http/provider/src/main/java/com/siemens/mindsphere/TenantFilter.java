package com.siemens.mindsphere;

import com.nimbusds.jwt.SignedJWT;
import de.fraunhofer.iais.eis.Message;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.dataspaceconnector.ids.jsonld.JsonLd;
import org.eclipse.dataspaceconnector.spi.iam.ClaimToken;
import org.eclipse.dataspaceconnector.spi.iam.TokenRepresentation;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.server.ContainerRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

@Provider
public class TenantFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private final Monitor monitor;

    public static final ThreadLocal<String> TLS = new InheritableThreadLocal<>();

    private final Map<String, String> tenants = Map.of("22:C8:23:9F:F1:92:57:36:C3:7B:43:CB:A4:DD:F7:BA:23:53:04:2C:keyid:22:C8:23:9F:F1:92:57:36:C3:7B:43:CB:A4:DD:F7:BA:23:53:04:2C", "castidev");

    public TenantFilter(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        byte[] entityData = null;
        try {
            if (!(requestContext instanceof ContainerRequest)) {
                monitor.debug("This is not an instance of ContainerRequest");
                return;
            }

            if (!((ContainerRequest) requestContext).getRequestUri().toString().endsWith("ids/data") || !requestContext.getMethod().equalsIgnoreCase("POST")) {
                monitor.debug("This is not the right endpoint");
                return;
            }

            entityData = ((ContainerRequest) requestContext).getEntityStream().readAllBytes();
            restoreRequestInputStream((ContainerRequest) requestContext, entityData);

            final FormDataMultiPart multiPart = ((ContainerRequest) requestContext).readEntity(FormDataMultiPart.class);

            if (multiPart.getField("header") == null) {
                monitor.debug("Does not have the header form");
                return;
            }

            final String headerValue = multiPart.getField("header").getValueAs(String.class);
            final Message header = JsonLd.getObjectMapper().readValue(headerValue, Message.class);

            var dynamicAttributeToken = header.getSecurityToken();
            if (dynamicAttributeToken == null || dynamicAttributeToken.getTokenValue() == null) {
                monitor.warning("Does not have the token in the header form");
                return;
            }

            var tokenRepresentation = TokenRepresentation.Builder.newInstance()
                    .token(dynamicAttributeToken.getTokenValue())
                    .build();

            var token = extractClaims(tokenRepresentation);
            var clientId = token.getContent().getClaims().get("client_id");
            var tenant = tenants.get(clientId);

            TLS.set(tenant);
            monitor.debug("TenantFilter called for " + tenant);
        } catch (Exception e) {
            monitor.severe("Failed to extract token", e);
        } finally {
            if (entityData != null) {
                restoreRequestInputStream((ContainerRequest) requestContext, entityData);
            }
        }
    }

    private void restoreRequestInputStream(ContainerRequest requestContext, byte[] entityData) {
        requestContext.setEntityStream(new ByteArrayInputStream(entityData));
    }

    public Result<ClaimToken> extractClaims(TokenRepresentation tokenRepresentation) {
        var token = tokenRepresentation.getToken();
        try {
            var signedJwt = SignedJWT.parse(token);
            var claimsSet = signedJwt.getJWTClaimsSet();

            var tokenBuilder = ClaimToken.Builder.newInstance();
            claimsSet.getClaims().entrySet().stream()
                    .map(entry -> Map.entry(entry.getKey(), Objects.toString(entry.getValue())))
                    .filter(entry -> entry.getValue() != null)
                    .forEach(entry -> tokenBuilder.claim(entry.getKey(), entry.getValue()));

            return Result.success(tokenBuilder.build());

        } catch (ParseException e) {
            return Result.failure("Failed to decode token");
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        TLS.remove();
        monitor.debug("TenantFilter cleaning up");
    }
}
