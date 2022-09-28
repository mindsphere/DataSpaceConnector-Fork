/*
 *  Copyright (c) 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - Initial implementation
 *
 */

package org.eclipse.dataspaceconnector.catalog.cache;

import org.eclipse.dataspaceconnector.catalog.cache.controller.FederatedCatalogApiController;
import org.eclipse.dataspaceconnector.catalog.cache.crawler.NodeQueryAdapterRegistryImpl;
import org.eclipse.dataspaceconnector.catalog.cache.query.CacheQueryAdapterImpl;
import org.eclipse.dataspaceconnector.catalog.cache.query.CacheQueryAdapterRegistryImpl;
import org.eclipse.dataspaceconnector.catalog.cache.query.IdsMultipartNodeQueryAdapter;
import org.eclipse.dataspaceconnector.catalog.cache.query.QueryEngineImpl;
import org.eclipse.dataspaceconnector.catalog.spi.CacheConfiguration;
import org.eclipse.dataspaceconnector.catalog.spi.CacheQueryAdapterRegistry;
import org.eclipse.dataspaceconnector.catalog.spi.CachedAsset;
import org.eclipse.dataspaceconnector.catalog.spi.FederatedCacheNodeDirectory;
import org.eclipse.dataspaceconnector.catalog.spi.FederatedCacheNodeFilter;
import org.eclipse.dataspaceconnector.catalog.spi.FederatedCacheStore;
import org.eclipse.dataspaceconnector.catalog.spi.NodeQueryAdapterRegistry;
import org.eclipse.dataspaceconnector.catalog.spi.QueryEngine;
import org.eclipse.dataspaceconnector.catalog.spi.model.ExecutionPlan;
import org.eclipse.dataspaceconnector.catalog.spi.model.UpdateResponse;
import org.eclipse.dataspaceconnector.runtime.metamodel.annotation.Inject;
import org.eclipse.dataspaceconnector.runtime.metamodel.annotation.Provider;
import org.eclipse.dataspaceconnector.spi.WebService;
import org.eclipse.dataspaceconnector.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.system.health.HealthCheckResult;
import org.eclipse.dataspaceconnector.spi.system.health.HealthCheckService;

import static java.util.Optional.ofNullable;

public class FederatedCatalogCacheExtension implements ServiceExtension {
    private Monitor monitor;
    @Inject
    private FederatedCacheStore store;
    @Inject
    private WebService webService;
    @Inject(required = false)
    private HealthCheckService healthCheckService;
    @Inject
    private RemoteMessageDispatcherRegistry dispatcherRegistry;
    // get all known nodes from node directory - must be supplied by another extension
    @Inject
    private FederatedCacheNodeDirectory directory;
    // optional filter function to select FC nodes eligible for crawling.
    @Inject(required = false)
    private FederatedCacheNodeFilter nodeFilter;

    private ExecutionPlan executionPlan;
    private NodeQueryAdapterRegistryImpl nodeQueryAdapterRegistry;
    private ExecutionManager executionManager;
    private CacheQueryAdapterRegistryImpl registry;
    private QueryEngineImpl queryEngine;

    @Override
    public String name() {
        return "Federated Catalog Cache";
    }

    @Provider
    public CacheQueryAdapterRegistry getCacheQueryAdapterRegistry() {
        if (registry == null) {
            registry = new CacheQueryAdapterRegistryImpl();
            registry.register(new CacheQueryAdapterImpl(store));
        }
        return registry;
    }

    @Provider
    public QueryEngine getQueryEngine() {
        if (queryEngine == null) {
            queryEngine = new QueryEngineImpl(getCacheQueryAdapterRegistry());
        }
        return queryEngine;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        // QUERY SUBSYSTEM

        monitor = context.getMonitor();
        var catalogController = new FederatedCatalogApiController(getQueryEngine());
        webService.registerResource(catalogController);

        // contribute to the liveness probe
        if (healthCheckService != null) {
            healthCheckService.addReadinessProvider(() -> HealthCheckResult.Builder.newInstance().component("FCC Query API").build());
        }

        // CRAWLER SUBSYSTEM
        var cacheConfiguration = new CacheConfiguration(context);
        int numCrawlers = cacheConfiguration.getNumCrawlers();
        // and a loader manager

        executionPlan = cacheConfiguration.getExecutionPlan();

        // by default only uses FC nodes that are not "self"
        nodeFilter = ofNullable(nodeFilter).orElse(node -> !node.getName().equals(context.getConnectorId()));

        executionManager = ExecutionManager.Builder.newInstance()
                .monitor(monitor)
                .preExecutionTask(() -> {
                    store.deleteExpired();
                    store.expireAll();
                })
                .numCrawlers(numCrawlers)
                .nodeQueryAdapterRegistry(createNodeQueryAdapterRegistry(context))
                .onSuccess(this::persist)
                .nodeDirectory(directory)
                .nodeFilterFunction(nodeFilter)
                .build();
    }

    @Override
    public void start() {
        executionManager.executePlan(executionPlan);
    }

    @Provider
    public NodeQueryAdapterRegistry createNodeQueryAdapterRegistry(ServiceExtensionContext context) {

        if (nodeQueryAdapterRegistry == null) {
            nodeQueryAdapterRegistry = new NodeQueryAdapterRegistryImpl();
            // catalog queries via IDS multipart are supported by default
            nodeQueryAdapterRegistry.register("ids-multipart", new IdsMultipartNodeQueryAdapter(context.getConnectorId(), dispatcherRegistry, monitor));
        }
        return nodeQueryAdapterRegistry;
    }

    /**
     * inserts a particular {@link org.eclipse.dataspaceconnector.spi.types.domain.catalog.Catalog} in the {@link FederatedCacheStore}
     *
     * @param updateResponse The response that contains the catalog
     */
    private void persist(UpdateResponse updateResponse) {
        updateResponse.getCatalog().getContractOffers().forEach(offer -> {
            offer.getAsset().getProperties().put(CachedAsset.PROPERTY_ORIGINATOR, updateResponse.getSource());
            store.save(offer);
        });
    }
}
