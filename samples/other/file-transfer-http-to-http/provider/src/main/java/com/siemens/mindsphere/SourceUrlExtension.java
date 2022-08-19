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


import com.siemens.mindsphere.datalake.edc.http.provision.MindsphereDatalakeSchema;
import com.siemens.mindsphere.tenant.PropertiesBasedTenantServiceImpl;
import com.siemens.mindsphere.tenant.SiemensCatalogServiceImpl;
import com.siemens.mindsphere.tenant.SiemensConnectorServiceImpl;
import com.siemens.mindsphere.tenant.TenantService;
import org.eclipse.dataspaceconnector.dataloading.AssetLoader;
import org.eclipse.dataspaceconnector.iam.oauth2.spi.Oauth2JwtDecoratorRegistry;
import org.eclipse.dataspaceconnector.ids.core.service.ConnectorServiceSettings;
import org.eclipse.dataspaceconnector.ids.spi.service.CatalogService;
import org.eclipse.dataspaceconnector.ids.spi.service.ConnectorService;
import org.eclipse.dataspaceconnector.policy.model.Action;
import org.eclipse.dataspaceconnector.policy.model.Permission;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.WebService;
import org.eclipse.dataspaceconnector.spi.asset.AssetSelectorExpression;
import org.eclipse.dataspaceconnector.spi.contract.offer.ContractOfferService;
import org.eclipse.dataspaceconnector.spi.contract.offer.store.ContractDefinitionStore;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.policy.PolicyDefinition;
import org.eclipse.dataspaceconnector.spi.policy.store.PolicyDefinitionStore;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Provides;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.types.domain.DataAddress;
import org.eclipse.dataspaceconnector.spi.types.domain.HttpDataAddress;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractDefinition;

import static org.eclipse.dataspaceconnector.ids.core.IdsCoreServiceExtension.DEFAULT_EDC_IDS_CATALOG_ID;


/**
 * It is possible to be called from outside without any JWT token passed
 */
@Provides({
        ConnectorService.class,
        CatalogService.class
})
public class SourceUrlExtension implements ServiceExtension {

    private static final Action USE_ACTION = Action.Builder.newInstance().type("USE").build();

    public static final String IDS_API_CONTEXT_ALIAS = "ids";

    @Inject
    private PolicyDefinitionStore policyStore;

    @Inject
    private ContractDefinitionStore contractStore;

    @Inject
    private AssetLoader loader;

    private static final String USE_POLICY = "use-eu";

    @EdcSetting
    private static final String EDC_ASSET_PATH = "edc.sample.path";

    @EdcSetting
    private static final String EDC_ASSET_URL = "edc.sample.url";

    @Inject
    private ContractOfferService contractOfferService;

    @Inject
    private Oauth2JwtDecoratorRegistry jwtDecoratorRegistry;

    @Inject
    private WebService webService;


    private Monitor monitor;
    private ConnectorServiceSettings connectorServiceSettings;

    @Override
    public void initialize(ServiceExtensionContext context) {
        monitor = context.getMonitor();
        connectorServiceSettings = new ConnectorServiceSettings(context, monitor);

        var tenantService = new PropertiesBasedTenantServiceImpl(context);
        var catalogService = catalogService(tenantService);

        context.registerService(TenantService.class, tenantService);
        context.registerService(CatalogService.class, catalogService);
        context.registerService(ConnectorService.class, connectorService(catalogService));

        var contextAlias = IDS_API_CONTEXT_ALIAS;
        webService.registerResource(contextAlias, new TenantFilter(monitor));

        addTestData(context);

    }

    public ConnectorService connectorService(CatalogService catalogService) {
        return new SiemensConnectorServiceImpl(monitor, connectorServiceSettings, catalogService);
    }

    public CatalogService catalogService(TenantService tenantService) {
        return new SiemensCatalogServiceImpl(DEFAULT_EDC_IDS_CATALOG_ID, contractOfferService, tenantService);
    }

    /**
     * Added only for testing
     * Use the below curl commands to test
     * <p>
     * curl -X POST -H "Content-Type: application/json" -H "X-Api-Key: passwordConsumer" -d @samples/other/file-transfer-http-to-http/datalakecontractoffer.json "http://localhost:9192/api/v1/data/contractnegotiations"
     * <p>
     * curl -X GET -H "Content-Type: application/json" -H "X-Api-Key: passwordConsumer"  "http://localhost:9192/api/v1/data/contractnegotiations"
     * <p>
     * curl -X POST -H "Content-Type: application/json" -H "X-Api-Key: passwordConsumer" -d @samples/other/file-transfer-http-to-http/datalaketransfer.json "http://localhost:9192/api/v1/data/transferprocess"
     */
    private void addTestData(ServiceExtensionContext context) {
        var policy = createPolicy();
        policyStore.save(policy);

        registerDataEntries(context);
        registerContractDefinition(policy.getUid());
    }

    @Override
    public String name() {
        return "Datalake Transfer With Provisioning";
    }

    private PolicyDefinition createPolicy() {
        var usePermission = Permission.Builder.newInstance()
                .action(USE_ACTION)
                .build();

        return PolicyDefinition.Builder.newInstance()
                .uid(USE_POLICY)
                .policy(Policy.Builder.newInstance()
                        .permission(usePermission)
                        .build())
                .build();
    }

    private void registerDataEntries(ServiceExtensionContext context) {
        var assetPathSetting = context.getSetting(EDC_ASSET_PATH, "data/ten=castidev/data.csv");

        var dataAddress = DataAddress.Builder.newInstance()
                .property("type", "HttpData")
                .property("ten", "castidev")
                .property("baseUrl", "http://fakesite.com")
                .property("method", "GET")
                .property("contentType", "text/csv")
                .property("nonChunkedTransfer", "true")
                .property(MindsphereDatalakeSchema.DOWNLOAD_DATALAKE_PATH, assetPathSetting)
                .build();

        var assetId = "data.csv";
        var asset = Asset.Builder.newInstance().id(assetId).property("tenant", "castiop").build();

        var assetUrl1 = context.getSetting(EDC_ASSET_URL, "https://raw.githubusercontent.com/eclipse-dataspaceconnector/DataSpaceConnector/main/styleguide.md");
        var dataAddress1 = DataAddress.Builder.newInstance()
                .property("type", HttpDataAddress.DATA_TYPE)
                .property("ten", "castiop")
                .property("baseUrl", assetUrl1)
                .property("method", "GET")
                .property("nonChunkedTransfer", "true")
                .property("contentType", "text/plain")
                .property("name", "")
                .build();

        var assetId1 = "styleguide.md";
        var asset1 = Asset.Builder.newInstance().id(assetId1).property("tenant", "castidev").build();

        var dataAddress2 = DataAddress.Builder.newInstance()
                .property("type", HttpDataAddress.DATA_TYPE)
                .property("baseUrl", "https://jsonplaceholder.typicode.com/todos/1")
                .property("ten", "presdev")
                .property("name", "")
                .property("nonChunkedTransfer", "true")
                .property("method", "GET")
                .property("contentType", "application/json")
                .build();

        var assetId2 = "1";
        var asset2 = Asset.Builder.newInstance().id(assetId2).property("tenant", "presdev").build();

        loader.accept(asset, dataAddress);
        loader.accept(asset1, dataAddress1);
        loader.accept(asset2, dataAddress2);
    }

    private void registerContractDefinition(String uid) {

        var contractDefinition9 = ContractDefinition.Builder.newInstance()
                .id("9")
                .accessPolicyId(uid)
                .contractPolicyId(uid)
                .selectorExpression(AssetSelectorExpression.Builder.newInstance().whenEquals(Asset.PROPERTY_ID, "data.csv").build())
                .build();

        var contractDefinition8 = ContractDefinition.Builder.newInstance()
                .id("8")
                .accessPolicyId(uid)
                .contractPolicyId(uid)
                .selectorExpression(AssetSelectorExpression.Builder.newInstance().whenEquals(Asset.PROPERTY_ID, "styleguide.md").build())
                .build();

        var contractDefinition7 = ContractDefinition.Builder.newInstance()
                .id("7")
                .accessPolicyId(uid)
                .contractPolicyId(uid)
                .selectorExpression(AssetSelectorExpression.Builder.newInstance().whenEquals(Asset.PROPERTY_ID, "1").build())
                .build();

        contractStore.save(contractDefinition9);
        contractStore.save(contractDefinition8);
        contractStore.save(contractDefinition7);
    }
}
