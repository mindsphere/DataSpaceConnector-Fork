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

package com.siemens.mindsphere;

import com.siemens.mindsphere.oauth.SiemensTokenGenerationServiceImpl;
import com.siemens.mindsphere.tenant.TenantFilter;
import com.siemens.mindsphere.tenant.TenantService;
import com.siemens.mindsphere.tenant.VaultBasedTenantServiceImpl;
import okhttp3.OkHttpClient;
import org.eclipse.dataspaceconnector.api.datamanagement.configuration.DataManagementApiConfiguration;
import org.eclipse.dataspaceconnector.common.token.TokenValidationServiceImpl;
import org.eclipse.dataspaceconnector.iam.oauth2.core.Oauth2Configuration;
import org.eclipse.dataspaceconnector.iam.oauth2.core.identity.IdentityProviderKeyResolver;
import org.eclipse.dataspaceconnector.iam.oauth2.core.identity.IdentityProviderKeyResolverConfiguration;
import org.eclipse.dataspaceconnector.iam.oauth2.core.rule.Oauth2ValidationRulesRegistryImpl;
import org.eclipse.dataspaceconnector.iam.oauth2.spi.Oauth2ValidationRulesRegistry;
import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.WebService;
import org.eclipse.dataspaceconnector.spi.iam.IdentityService;
import org.eclipse.dataspaceconnector.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.security.CertificateResolver;
import org.eclipse.dataspaceconnector.spi.security.PrivateKeyResolver;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.spi.security.VaultCertificateResolver;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Provider;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

import java.time.Clock;

@Provides({ IdentityService.class, TenantService.class })
public class SiemensOauthExtension implements ServiceExtension {

    public static final String IDS_API_CONTEXT_ALIAS = "ids";

    @EdcSetting
    private static final String PROVIDER_JWKS_URL = "edc.oauth.provider.jwks.url";

    @EdcSetting
    private static final String PROVIDER_AUDIENCE = "edc.oauth.provider.audience";

    @EdcSetting
    private static final String PUBLIC_KEY_ALIAS = "edc.oauth.public.key.alias";

    @EdcSetting
    private static final String PRIVATE_KEY_ALIAS = "edc.oauth.private.key.alias";

    @EdcSetting
    private static final String PROVIDER_JWKS_REFRESH = "edc.oauth.provider.jwks.refresh"; // in minutes

    @EdcSetting
    private static final String TOKEN_URL = "edc.oauth.token.url";

    @EdcSetting
    private static final String CLIENT_ID = "edc.oauth.client.id";

    @EdcSetting
    private static final String NOT_BEFORE_LEEWAY = "edc.oauth.validation.nbf.leeway";

    private IdentityProviderKeyResolver providerKeyResolver;

    @Inject
    private OkHttpClient okHttpClient;

    @Inject
    private PrivateKeyResolver privateKeyResolver;

    private CertificateResolver certificateResolver;

    @Inject
    private Clock clock;

    @Inject
    private Vault vault;

    @Inject
    private RemoteMessageDispatcherRegistry dispatcherRegistry;

    @Inject
    private WebService webService;

    @Inject
    private DataManagementApiConfiguration config;

    private Monitor monitor;

    @Override
    public String name() {
        return "SiemensOAuth2";
    }

    @Provider(isDefault = true)
    public CertificateResolver certificateResolver() {
        return certificateResolver;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        monitor = context.getMonitor();

        certificateResolver = new VaultCertificateResolver(vault);

        context.registerService(CertificateResolver.class, certificateResolver);

        var jwksUrl = context.getSetting(PROVIDER_JWKS_URL, "http://localhost/empty_jwks_url");
        var keyRefreshInterval = context.getSetting(PROVIDER_JWKS_REFRESH, 5);
        var identityProviderKeyResolverConfiguration = new IdentityProviderKeyResolverConfiguration(jwksUrl, keyRefreshInterval);
        providerKeyResolver = new IdentityProviderKeyResolver(context.getMonitor(), okHttpClient, context.getTypeManager(), identityProviderKeyResolverConfiguration);

        var tenantService = new VaultBasedTenantServiceImpl(vault, privateKeyResolver, certificateResolver, context);
        context.registerService(TenantService.class, tenantService);

        var configuration = createConfig(context);

        var validationRulesRegistry = new Oauth2ValidationRulesRegistryImpl(configuration, clock);
        context.registerService(Oauth2ValidationRulesRegistry.class, validationRulesRegistry);

        var tokenValidationService = new TokenValidationServiceImpl(configuration.getIdentityProviderKeyResolver(), validationRulesRegistry);

        var oauth2Service = new com.siemens.mindsphere.oauth.SiemensOauth2ServiceImpl(
                context.getMonitor(),
                configuration,
                new SiemensTokenGenerationServiceImpl(tenantService),
                okHttpClient,
                context.getTypeManager(),
                tokenValidationService,
                tenantService,
                clock);

        context.registerService(IdentityService.class, oauth2Service);

        var tenantFilter = new TenantFilter(monitor);
        webService.registerResource(IDS_API_CONTEXT_ALIAS, tenantFilter);
        webService.registerResource(config.getContextAlias(), tenantFilter);
    }

    @Override
    public void start() {
        providerKeyResolver.start();
    }

    @Override
    public void shutdown() {
        providerKeyResolver.stop();
    }


    private Oauth2Configuration createConfig(ServiceExtensionContext context) {
        var providerAudience = context.getSetting(PROVIDER_AUDIENCE, context.getConnectorId());
        var tokenUrl = context.getConfig().getString(TOKEN_URL);
        var publicKeyAlias = context.getConfig().getString(PUBLIC_KEY_ALIAS);
        var privateKeyAlias = context.getConfig().getString(PRIVATE_KEY_ALIAS);
        var clientId = context.getConfig().getString(CLIENT_ID);
        return Oauth2Configuration.Builder.newInstance()
                .identityProviderKeyResolver(providerKeyResolver)
                .tokenUrl(tokenUrl)
                .providerAudience(providerAudience)
                .publicCertificateAlias(publicKeyAlias)
                .privateKeyAlias(privateKeyAlias)
                .clientId(clientId)
                .privateKeyResolver(privateKeyResolver)
                .certificateResolver(certificateResolver)
                .notBeforeValidationLeeway(context.getSetting(NOT_BEFORE_LEEWAY, 10))
                .build();
    }
}
