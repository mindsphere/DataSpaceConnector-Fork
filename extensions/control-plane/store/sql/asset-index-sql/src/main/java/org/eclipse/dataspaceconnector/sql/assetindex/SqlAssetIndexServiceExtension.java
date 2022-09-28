/*
 *  Copyright (c) 2022 Daimler TSS GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Daimler TSS GmbH - Initial API and Implementation
 *       Microsoft Corporation - Refactoring and improvements
 *
 */

package org.eclipse.dataspaceconnector.sql.assetindex;

import org.eclipse.dataspaceconnector.runtime.metamodel.annotation.Inject;
import org.eclipse.dataspaceconnector.runtime.metamodel.annotation.Provides;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.asset.DataAddressResolver;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transaction.TransactionContext;
import org.eclipse.dataspaceconnector.spi.transaction.datasource.DataSourceRegistry;
import org.eclipse.dataspaceconnector.sql.assetindex.schema.AssetStatements;
import org.eclipse.dataspaceconnector.sql.assetindex.schema.postgres.PostgresDialectStatements;

@Provides({ AssetIndex.class, DataAddressResolver.class })
public class SqlAssetIndexServiceExtension implements ServiceExtension {

    @Inject
    private DataSourceRegistry dataSourceRegistry;

    @Inject
    private TransactionContext transactionContext;

    @Inject(required = false)
    private AssetStatements dialect;

    @Override
    public void initialize(ServiceExtensionContext context) {
        var dataSourceName = context.getConfig().getString(ConfigurationKeys.DATASOURCE_SETTING_NAME);

        var sqlAssetLoader = new SqlAssetIndex(dataSourceRegistry, dataSourceName, transactionContext, context.getTypeManager().getMapper(), getDialect());

        context.registerService(AssetIndex.class, sqlAssetLoader);
        context.registerService(DataAddressResolver.class, sqlAssetLoader);
    }

    private AssetStatements getDialect() {
        return dialect != null ? dialect : new PostgresDialectStatements();
    }
}
