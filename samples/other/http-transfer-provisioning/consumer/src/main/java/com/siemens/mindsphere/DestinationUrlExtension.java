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



import com.siemens.mindsphere.provision.DestinationUrlProvisionedResource;
import com.siemens.mindsphere.provision.DestinationUrlProvisioner;
import com.siemens.mindsphere.provision.DestinationUrlResourceDefinition;
import com.siemens.mindsphere.provision.DestinationUrlResourceDefinitionGenerator;
import net.jodah.failsafe.RetryPolicy;
import org.eclipse.dataspaceconnector.dataloading.AssetLoader;
import org.eclipse.dataspaceconnector.dataplane.spi.pipeline.DataTransferExecutorServiceContainer;
import org.eclipse.dataspaceconnector.dataplane.spi.pipeline.PipelineService;
import org.eclipse.dataspaceconnector.policy.model.Action;
import org.eclipse.dataspaceconnector.policy.model.Permission;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.asset.AssetSelectorExpression;
import org.eclipse.dataspaceconnector.spi.contract.offer.store.ContractDefinitionStore;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.policy.store.PolicyStore;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transfer.provision.ProvisionManager;
import org.eclipse.dataspaceconnector.spi.transfer.provision.ResourceManifestGenerator;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;
import org.eclipse.dataspaceconnector.spi.types.domain.DataAddress;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractDefinition;
import org.eclipse.dataspaceconnector.spi.types.domain.http.HttpDataAddressSchema;

public class DestinationUrlExtension implements ServiceExtension {

    public static final String USE_POLICY = "use-eu";

    @EdcSetting
    private static final String EDC_ASSET_PATH = "edc.samples.datalake.asset.path";

    @Inject
    private ResourceManifestGenerator manifestGenerator;

    @Inject
    private PolicyStore policyStore;

    @Inject
    private ContractDefinitionStore contractStore;

    @Inject
    private AssetLoader loader;

    @Inject
    private PipelineService pipelineService;

    @Inject
    private DataTransferExecutorServiceContainer executorContainer;


    @Override
    public void initialize(ServiceExtensionContext context) {
        Monitor monitor = context.getMonitor();

        // register provisioner
        @SuppressWarnings("unchecked") var retryPolicy = (RetryPolicy<Object>) context.getService(RetryPolicy.class);
        var provisionManager = context.getService(ProvisionManager.class);
        final DestinationUrlProvisioner sourceUrlProvisioner = new DestinationUrlProvisioner(context, retryPolicy);
        provisionManager.register(sourceUrlProvisioner);

        var policy = createPolicy();
        policyStore.save(policy);

        registerDataEntries(context);
        registerContractDefinition(policy.getUid());

        // register the datalake resource definition generator
        manifestGenerator.registerGenerator(new DestinationUrlResourceDefinitionGenerator(monitor, context));

        // register provision specific classes
        registerTypes(context.getTypeManager());
    }

    @Override
    public String name() {
        return "Datalake Consumer With Provisioning";
    }

    private void registerTypes(TypeManager typeManager) {
        typeManager.registerTypes(DestinationUrlProvisionedResource.class, DestinationUrlResourceDefinition.class);
    }

    private Policy createPolicy() {

        var usePermission = Permission.Builder.newInstance()
                .action(Action.Builder.newInstance().type("USE").build())
                .build();

        return Policy.Builder.newInstance()
                .id(USE_POLICY)
                .permission(usePermission)
                .build();
    }

    private void registerDataEntries(ServiceExtensionContext context) {
        var assetPathSetting = context.getSetting(EDC_ASSET_PATH, "data/ten=castidev/data.csv");

        var dataAddress = DataAddress.Builder.newInstance()
                .property("type", HttpDataAddressSchema.TYPE)
                .property("datalakepath", assetPathSetting)
                .build();

        var assetId = "data.csv";
        var asset = Asset.Builder.newInstance().id(assetId).build();

        loader.accept(asset, dataAddress);
    }

    private void registerContractDefinition(String uid) {

        var contractDefinition = ContractDefinition.Builder.newInstance()
                .id("3")
                .accessPolicyId(uid)
                .contractPolicyId(uid)
                .selectorExpression(AssetSelectorExpression.Builder.newInstance().whenEquals(Asset.PROPERTY_ID, "data.csv").build())
                .build();

        contractStore.save(contractDefinition);
    }
}
