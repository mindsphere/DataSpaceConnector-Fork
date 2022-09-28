/*
 *  Copyright (c) 2020 - 2022 Microsoft Corporation
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

package org.eclipse.dataspaceconnector.contract.definition.store;

import dev.failsafe.RetryPolicy;
import org.eclipse.dataspaceconnector.azure.cosmos.CosmosClientProvider;
import org.eclipse.dataspaceconnector.azure.cosmos.CosmosDbApiImpl;
import org.eclipse.dataspaceconnector.cosmos.policy.store.model.ContractDefinitionDocument;
import org.eclipse.dataspaceconnector.runtime.metamodel.annotation.Inject;
import org.eclipse.dataspaceconnector.runtime.metamodel.annotation.Provides;
import org.eclipse.dataspaceconnector.spi.contract.offer.store.ContractDefinitionStore;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.system.health.HealthCheckService;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;

@Provides({ ContractDefinitionStore.class })
public class CosmosContractDefinitionStoreExtension implements ServiceExtension {

    @Inject
    private Vault vault;

    @Inject
    private CosmosClientProvider clientProvider;

    @Inject
    private Monitor monitor;

    @Inject
    private RetryPolicy<Object> retryPolicy;

    @Inject
    private TypeManager typeManager;

    @Override
    public String name() {
        return "CosmosDB ContractDefinition Store";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var configuration = new CosmosContractDefinitionStoreConfig(context);

        var client = clientProvider.createClient(vault, configuration);
        var cosmosDbApi = new CosmosDbApiImpl(configuration, client);

        var store = new CosmosContractDefinitionStore(cosmosDbApi, typeManager, retryPolicy, configuration.getPartitionKey(), monitor);
        context.registerService(ContractDefinitionStore.class, store);

        context.getTypeManager().registerTypes(ContractDefinitionDocument.class);

        context.getService(HealthCheckService.class).addReadinessProvider(() -> cosmosDbApi.get().forComponent(name()));

    }

}

