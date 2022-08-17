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

package org.eclipse.dataspaceconnector.core.defaults;

import dev.failsafe.RetryPolicy;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import org.eclipse.dataspaceconnector.common.concurrency.LockManager;
import org.eclipse.dataspaceconnector.core.base.OkHttpClientFactory;
import org.eclipse.dataspaceconnector.core.defaults.assetindex.InMemoryAssetIndex;
import org.eclipse.dataspaceconnector.core.defaults.contractdefinition.InMemoryContractDefinitionStore;
import org.eclipse.dataspaceconnector.core.defaults.negotiationstore.InMemoryContractNegotiationStore;
import org.eclipse.dataspaceconnector.core.defaults.policystore.InMemoryPolicyDefinitionStore;
import org.eclipse.dataspaceconnector.core.defaults.transferprocessstore.InMemoryTransferProcessStore;
import org.eclipse.dataspaceconnector.dataloading.AssetLoader;
import org.eclipse.dataspaceconnector.dataloading.ContractDefinitionLoader;
import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.asset.DataAddressResolver;
import org.eclipse.dataspaceconnector.spi.contract.negotiation.store.ContractNegotiationStore;
import org.eclipse.dataspaceconnector.spi.contract.offer.store.ContractDefinitionStore;
import org.eclipse.dataspaceconnector.spi.policy.store.PolicyDefinitionStore;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.Provider;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transaction.NoopTransactionContext;
import org.eclipse.dataspaceconnector.spi.transaction.TransactionContext;
import org.eclipse.dataspaceconnector.spi.transfer.store.TransferProcessStore;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Provides (in-mem &amp; no-op) defaults for various stores, registries etc.
 * Provider methods are only invoked if no other implementation was found on the classpath.
 */
public class DefaultServicesExtension implements ServiceExtension {
    @EdcSetting(value = "Maximum retries for the retry policy before a failure is propagated")
    public static final String MAX_RETRIES = "edc.core.retry.retries.max";
    @EdcSetting(value = "Minimum number of milliseconds for exponential backoff")
    public static final String BACKOFF_MIN_MILLIS = "edc.core.retry.backoff.min";
    @EdcSetting(value = "Maximum number of milliseconds for exponential backoff. ")
    public static final String BACKOFF_MAX_MILLIS = "edc.core.retry.backoff.max";

    private InMemoryAssetIndex assetIndex;
    private InMemoryContractDefinitionStore contractDefinitionStore;
    /**
     * An optional OkHttp {@link EventListener} that can be used to instrument OkHttp client for collecting metrics.
     * Used by the optional {@code micrometer} module.
     */
    @Inject(required = false)
    private EventListener okHttpEventListener;

    public DefaultServicesExtension() {
    }

    @Provider
    public RetryPolicy<?> retryPolicy(ServiceExtensionContext context) {
        var maxRetries = context.getSetting(MAX_RETRIES, 5);
        var minBackoff = context.getSetting(BACKOFF_MIN_MILLIS, 500);
        var maxBackoff = context.getSetting(BACKOFF_MAX_MILLIS, 10_000);

        return RetryPolicy.builder()
                .withMaxRetries(maxRetries)
                .withBackoff(minBackoff, maxBackoff, ChronoUnit.MILLIS)
                .build();
    }

    @Provider(isDefault = true)
    public AssetIndex defaultAssetIndex() {
        return getAssetIndex();
    }

    @Provider(isDefault = true)
    public DataAddressResolver defaultDataAddressResolver() {
        return getAssetIndex();
    }

    @Provider(isDefault = true)
    public AssetLoader defaultAssetLoader() {
        return getAssetIndex();
    }

    @Provider(isDefault = true)
    public ContractDefinitionStore defaultContractDefinitionStore() {
        return getContractDefinitionStore();
    }

    @Provider(isDefault = true)
    public ContractDefinitionLoader defaultContractDefinitionLoader() {
        return getContractDefinitionStore()::save;
    }

    @Provider(isDefault = true)
    public ContractNegotiationStore defaultContractNegotiationStore() {
        return new InMemoryContractNegotiationStore();
    }

    @Provider(isDefault = true)
    public TransferProcessStore defaultTransferProcessStore() {
        return new InMemoryTransferProcessStore();
    }

    @Provider(isDefault = true)
    public PolicyDefinitionStore defaultPolicyStore() {
        return new InMemoryPolicyDefinitionStore(new LockManager(new ReentrantReadWriteLock(true)));
    }

    @Provider(isDefault = true)
    public TransactionContext defaultTransactionContext(ServiceExtensionContext context) {
        context.getMonitor().warning("No TransactionContext registered, a no-op implementation will be used, not suitable for production environments");
        return new NoopTransactionContext();
    }


    @Provider
    public OkHttpClient okHttpClient(ServiceExtensionContext context) {
        return OkHttpClientFactory.create(context, okHttpEventListener);
    }

    private ContractDefinitionStore getContractDefinitionStore() {
        if (contractDefinitionStore == null) {
            contractDefinitionStore = new InMemoryContractDefinitionStore();
        }
        return contractDefinitionStore;
    }

    private InMemoryAssetIndex getAssetIndex() {
        if (assetIndex == null) {
            assetIndex = new InMemoryAssetIndex();
        }
        return assetIndex;
    }
}
