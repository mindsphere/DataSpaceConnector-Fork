/*
 *  Copyright (c) 2020, 2021 Microsoft Corporation
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

package com.siemens.mindsphere;

import com.siemens.mindsphere.tenant.TenantService;
import org.eclipse.dataspaceconnector.core.defaults.assetindex.InMemoryAssetIndex;
import org.eclipse.dataspaceconnector.spi.query.QuerySpec;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * An ephemeral asset index, that is also a DataAddressResolver and an AssetLoader
 */
public class SiemensInMemoryAssetIndex extends InMemoryAssetIndex {

    @Override
    public Stream<Asset> queryAssets(QuerySpec querySpec) {
        final Stream<Asset> assetStream = super.queryAssets(querySpec);
        final var tenant = TenantService.TLS_TENANT.get();

        return assetStream.filter(asset -> Objects.equals(asset.getProperty("tenant"), tenant));
    }
}
