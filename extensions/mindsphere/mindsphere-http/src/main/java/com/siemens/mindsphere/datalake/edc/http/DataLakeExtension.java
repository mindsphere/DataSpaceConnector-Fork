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
 *       Microsoft Corporation - initial API and implementation
 *
 */

package com.siemens.mindsphere.datalake.edc.http;

import dev.failsafe.RetryPolicy;
import com.siemens.mindsphere.datalake.edc.http.provision.SourceUrlProvisionedResource;
import com.siemens.mindsphere.datalake.edc.http.provision.SourceUrlProvisioner;
import com.siemens.mindsphere.datalake.edc.http.provision.SourceUrlResourceDefinition;
import com.siemens.mindsphere.datalake.edc.http.provision.SourceUrlResourceDefinitionGenerator;
import okhttp3.OkHttpClient;
import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transfer.provision.ProvisionManager;
import org.eclipse.dataspaceconnector.spi.transfer.provision.ResourceManifestGenerator;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;

import java.net.MalformedURLException;
import java.net.URL;

public class DataLakeExtension implements ServiceExtension {
    @EdcSetting
    private static final String STUB_URL = "edc.demo.http.destination.url";

    @Inject
    private ResourceManifestGenerator manifestGenerator;

    @Inject
    private OkHttpClient httpClient;

    @EdcSetting
    private static final String TOKENMANAGEMENT_ADDRESS = "datalake.oauth.address";

    @EdcSetting
    private static final String TOKENMANAGEMENT_CLIENT_ID = "datalake.oauth.client.id";

    @EdcSetting
    private static final String TOKENMANAGEMENT_CLIENT_SECRET = "datalake.oauth.client.secret";

    @EdcSetting
    private static final String TOKENMANAGEMENT_CLIENT_APP_NAME = "datalake.oauth.client.app.name";

    @EdcSetting
    private static final String TOKENMANAGEMENT_CLIENT_APP_VERSION = "datalake.oauth.client.app.version";

    @EdcSetting
    private static final String APPLICATION_TENANT = "datalake.tenant";

    @EdcSetting
    private static final String DATALAKE_BASE_URL = "datalake.base.url.endpoint";

    @Override
    public void initialize(ServiceExtensionContext context) {
        Monitor monitor = context.getMonitor();

        try {
            final String tokenmanagementClientId = context.getSetting(TOKENMANAGEMENT_CLIENT_ID, "");
            final String tokenmanagementClientSecret = context.getSetting(TOKENMANAGEMENT_CLIENT_SECRET, "");
            final String tokenmanagementClientAppName = context.getSetting(TOKENMANAGEMENT_CLIENT_APP_NAME, "");
            final String tokenmanagementClientAppVersion = context.getSetting(TOKENMANAGEMENT_CLIENT_APP_VERSION, "");
            final String tokenmanagementAddress = context.getSetting(TOKENMANAGEMENT_ADDRESS, "");
            final String dataLakeAddress = context.getSetting(DATALAKE_BASE_URL, "");

            final String applicationTenant = context.getSetting(APPLICATION_TENANT, "presdev");

            monitor.debug("datalake.oauth.clientId=" + tokenmanagementClientId.substring(0, 5));
            monitor.debug("datalake.oauth.clientSecret=" + tokenmanagementClientSecret.substring(0, 5));
            monitor.debug("datalake.tenant=" + applicationTenant);
            monitor.debug("datalake.address=" + dataLakeAddress);

            final URL url = new URL(dataLakeAddress);

            final OauthClientDetails oauthClientDetails = new OauthClientDetails(tokenmanagementClientId, tokenmanagementClientSecret,
                    tokenmanagementClientAppName, tokenmanagementClientAppVersion, applicationTenant, new URL(tokenmanagementAddress));
            final DataLakeClientImpl clientImpl = new DataLakeClientImpl(oauthClientDetails, url);


            registerProvisioners(context, monitor, clientImpl);
            registerProvisionDefinitionsGenerators(context, monitor);
            registerProvisionTypes(context.getTypeManager());

        } catch (MalformedURLException e) {
            monitor.severe("Failed to register datalake source url provisioning due to exception ", e);
        }
    }

    private void registerProvisionDefinitionsGenerators(ServiceExtensionContext context, Monitor monitor) {
        // register the datalake resource definition generator
        manifestGenerator.registerGenerator(new SourceUrlResourceDefinitionGenerator(monitor, context));
    }

    private void registerProvisioners(ServiceExtensionContext context, Monitor monitor, DataLakeClientImpl clientImpl) {
        @SuppressWarnings("unchecked") var retryPolicy = context.getService(RetryPolicy.class);
        var provisionManager = context.getService(ProvisionManager.class);

        final SourceUrlProvisioner sourceUrlProvisioner = new SourceUrlProvisioner(clientImpl, context, retryPolicy);
        provisionManager.register(sourceUrlProvisioner);
    }

    private void registerProvisionTypes(TypeManager typeManager) {
        typeManager.registerTypes(
                SourceUrlProvisionedResource.class, SourceUrlResourceDefinition.class);
    }

    @Override
    public String name() {
        return "MindSphere DataLake";
    }
}
