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

import org.eclipse.dataspaceconnector.dataloading.AssetLoader;
import org.eclipse.dataspaceconnector.dataloading.ContractDefinitionLoader;
import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.asset.DataAddressResolver;
import org.eclipse.dataspaceconnector.spi.persistence.EdcPersistenceException;
import org.eclipse.dataspaceconnector.spi.policy.store.PolicyDefinitionStore;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transaction.TransactionContext;
import org.eclipse.dataspaceconnector.spi.transaction.datasource.DataSourceRegistry;
import org.eclipse.dataspaceconnector.spi.transfer.flow.DataFlowManager;
import org.eclipse.dataspaceconnector.spi.transfer.inline.DataOperatorRegistry;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

public class TransferExtension implements ServiceExtension {
    @EdcSetting
    private static final String STUB_URL = "edc.demo.http.source.url";

    @Inject
    private DataFlowManager dataFlowMgr;
    @Inject
    private DataAddressResolver dataAddressResolver;
    @Inject
    private DataOperatorRegistry dataOperatorRegistry;
    @Inject
    private static DataSourceRegistry dataSourceRegistry;
    @Inject
    private ContractDefinitionLoader contractDefinitionLoader;
    @Inject
    private PolicyDefinitionStore policyStore;
    @Inject
    private AssetLoader assetLoader;
    @Inject
    private TransactionContext transactionContext;

    @EdcSetting
    private static final String DB_USER = "edc.datasource.db.user.";
    @EdcSetting
    private static final String DB_PASSWORD = "edc.datasource.db.password";
    @EdcSetting
    private static final String JDBC_URL_PREFIX = "edc.datasource.jdbc.prefix";

    private static final String DEFAULT_DB_USER = "postgres";
    private static final String DEFAULT_DB_PASSWORD = "password";
    private static final String DEFAULT_JDBC_URL_PREFIX = "jdbc:postgresql://localhost:5432/";

    @Override
    public void initialize(ServiceExtensionContext context) {
        // var vault = context.getService(Vault.class);
        var monitor = context.getMonitor();

        // //Comment the below 3 lines out to remove the old way of copying files
        // dataOperatorRegistry.registerWriter(new HttpWriter(monitor));
        // dataOperatorRegistry.registerReader(new HttpReader(monitor));

        // dataFlowMgr.register(new InlineDataFlowController(vault,
        // context.getMonitor(), dataOperatorRegistry));

        try {
            monitor.info("Initialize postgresql databases");
            prepareDatabase("provider", context);
        } catch (ClassNotFoundException e) {
            monitor.severe("Error initializing postgresql driver", e);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        monitor.info("HTTP Transfer Extension initialized!");
    }

    static void createDatabase(String name, String jdbcUrlPrefix, String dbUser, String dbPassword) {
        try (var connection = DriverManager.getConnection(jdbcUrlPrefix + dbUser, dbUser, dbPassword)) {
            connection.createStatement().execute(format("create database %s;", name));
        } catch (SQLException e) {
            // database could already exist
        }
    }

    private static void prepareDatabase(String provider, ServiceExtensionContext context)
            throws ClassNotFoundException, SQLException, IOException {
        Class.forName("org.postgresql.Driver");

        String jdbcUrlPrefix = context.getSetting(JDBC_URL_PREFIX, DEFAULT_JDBC_URL_PREFIX);
        String dbUser = context.getSetting(DB_USER, DEFAULT_DB_USER);
        String dbPassword = context.getSetting(DB_PASSWORD, DEFAULT_DB_PASSWORD);

        var pgSimpleDataSource = new PGSimpleDataSource();
        pgSimpleDataSource.setURL(jdbcUrlPrefix + dbUser);

        dataSourceRegistry.register("postgres", pgSimpleDataSource);

        createDatabase(provider, jdbcUrlPrefix, dbUser, dbPassword);

        var scripts = Stream.of(
                "asset-index-sql",
                "contract-definition-store-sql",
                "policy-store-sql")
                .map(module -> "./" + module + "/schema.sql")
                .map(Paths::get)
                .collect(Collectors.toList());

        try (Connection connection = DriverManager.getConnection(jdbcUrlPrefix + dbUser, dbUser, dbPassword)) {
            for (var script : scripts) {
                var sql = Files.readString(script);

                try (var statement = connection.createStatement()) {
                    statement.execute(sql);
                } catch (Exception exception) {
                    throw new EdcPersistenceException(exception.getMessage(), exception);
                }
            }
        }
    }

}
