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

package org.eclipse.dataspaceconnector.cosmos.policy.store;

import dev.failsafe.RetryPolicy;
import org.eclipse.dataspaceconnector.azure.cosmos.CosmosClientProvider;
import org.eclipse.dataspaceconnector.azure.cosmos.CosmosDbApiImpl;
import org.eclipse.dataspaceconnector.runtime.metamodel.annotation.Inject;
import org.eclipse.dataspaceconnector.runtime.metamodel.annotation.Provides;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.policy.store.PolicyDefinitionStore;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.system.health.HealthCheckService;
import org.eclipse.dataspaceconnector.spi.types.TypeManager;

@Provides({ PolicyDefinitionStore.class })
public class CosmosPolicyStoreExtension implements ServiceExtension {

    @Inject
    private RetryPolicy<Object> retryPolicy;

    @Inject
    private TypeManager typeManager;

    @Inject
    private Monitor monitor;
    @Inject
    private CosmosClientProvider clientProvider;

    @Override
    public String name() {
        return "CosmosDB Policy Store";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var configuration = new CosmosPolicyStoreConfig(context);

        var vault = context.getService(Vault.class);

        var cosmosDbApi = new CosmosDbApiImpl(configuration, clientProvider.createClient(vault, configuration));

        var store = new CosmosPolicyDefinitionStore(cosmosDbApi, typeManager, retryPolicy, configuration.getPartitionKey(), monitor);
        context.registerService(PolicyDefinitionStore.class, store);

        context.getTypeManager().registerTypes(PolicyDocument.class);

        context.getService(HealthCheckService.class).addReadinessProvider(() -> cosmosDbApi.get().forComponent(name()));

    }

}

