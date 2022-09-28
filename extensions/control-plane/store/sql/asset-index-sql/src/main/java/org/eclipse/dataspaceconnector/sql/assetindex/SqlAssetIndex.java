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
 *       Microsoft Corporation - added full QuerySpec support
 *
 */

package org.eclipse.dataspaceconnector.sql.assetindex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.asset.AssetSelectorExpression;
import org.eclipse.dataspaceconnector.spi.persistence.EdcPersistenceException;
import org.eclipse.dataspaceconnector.spi.query.QuerySpec;
import org.eclipse.dataspaceconnector.spi.transaction.TransactionContext;
import org.eclipse.dataspaceconnector.spi.transaction.datasource.DataSourceRegistry;
import org.eclipse.dataspaceconnector.spi.types.domain.DataAddress;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.AssetEntry;
import org.eclipse.dataspaceconnector.sql.assetindex.schema.AssetStatements;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.sql.DataSource;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.eclipse.dataspaceconnector.sql.SqlQueryExecutor.executeQuery;

public class SqlAssetIndex implements AssetIndex {

    private final ObjectMapper objectMapper;
    private final DataSourceRegistry dataSourceRegistry;
    private final String dataSourceName;
    private final TransactionContext transactionContext;
    private final AssetStatements assetStatements;

    public SqlAssetIndex(DataSourceRegistry dataSourceRegistry, String dataSourceName, TransactionContext transactionContext, ObjectMapper objectMapper, AssetStatements assetStatements) {
        this.dataSourceRegistry = Objects.requireNonNull(dataSourceRegistry);
        this.dataSourceName = Objects.requireNonNull(dataSourceName);
        this.transactionContext = Objects.requireNonNull(transactionContext);
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.assetStatements = Objects.requireNonNull(assetStatements);
    }

    @Override
    public Stream<Asset> queryAssets(AssetSelectorExpression expression) {
        Objects.requireNonNull(expression);

        var criteria = expression.getCriteria();
        var querySpec = QuerySpec.Builder.newInstance().filter(criteria)
                .offset(0)
                .limit(Integer.MAX_VALUE) // means effectively no limit
                .build();
        return queryAssets(querySpec);
    }

    @Override
    public Stream<Asset> queryAssets(QuerySpec querySpec) {
        Objects.requireNonNull(querySpec);

        return transactionContext.execute(() -> {
            try (var connection = getConnection()) {

                var statement = assetStatements.createQuery(querySpec);

                var ids = executeQuery(connection, this::mapAssetIds, statement.getQueryAsString(), statement.getParameters());
                return ids.stream().map(this::findById);
            } catch (SQLException e) {
                throw new EdcPersistenceException(e);
            }
        });
    }

    @Override
    public @Nullable Asset findById(String assetId) {
        Objects.requireNonNull(assetId);

        try (var connection = getConnection()) {

            return transactionContext.execute(() -> {
                if (!existsById(assetId, connection)) {
                    return null;
                }
                var ts = single(executeQuery(connection, resultSet -> resultSet.getLong(assetStatements.getCreatedAtColumn()), assetStatements.getSelectAssetByIdTemplate(), assetId));
                var assetProperties = executeQuery(connection, this::mapPropertyResultSet, assetStatements.getFindPropertyByIdTemplate(), assetId).stream().collect(Collectors.toMap(
                        AbstractMap.SimpleImmutableEntry::getKey,
                        AbstractMap.SimpleImmutableEntry::getValue));
                return Asset.Builder.newInstance().id(assetId).properties(assetProperties).createdAt(ofNullable(ts).orElse(0L)).build();
            });

        } catch (Exception e) {
            if (e instanceof EdcPersistenceException) {
                throw (EdcPersistenceException) e;
            } else {
                throw new EdcPersistenceException(e.getMessage(), e);
            }
        }
    }

    @Override
    public void accept(AssetEntry item) {
        Objects.requireNonNull(item);
        var asset = item.getAsset();
        var dataAddress = item.getDataAddress();

        Objects.requireNonNull(asset);
        Objects.requireNonNull(dataAddress);


        var assetId = asset.getId();
        transactionContext.execute(() -> {
            try (var connection = getConnection()) {
                try {
                    if (existsById(assetId, connection)) {
                        deleteById(assetId);
                    }

                    executeQuery(connection, assetStatements.getInsertAssetTemplate(), assetId, asset.getCreatedAt());
                    var insertDataAddressTemplate = assetStatements.getInsertDataAddressTemplate();
                    executeQuery(connection, insertDataAddressTemplate, assetId, objectMapper.writeValueAsString(dataAddress.getProperties()));

                    for (var property : asset.getProperties().entrySet()) {
                        executeQuery(connection, assetStatements.getInsertPropertyTemplate(),
                                assetId,
                                property.getKey(),
                                toPropertyValue(property.getValue()),
                                property.getValue().getClass().getName());
                    }

                } catch (JsonProcessingException e) {
                    throw new EdcPersistenceException(e);
                }
            } catch (Exception e) {
                throw new EdcPersistenceException(e);
            }
        });


    }

    @Override
    public Asset deleteById(String assetId) {
        Objects.requireNonNull(assetId);

        try (var connection = getConnection()) {
            var asset = findById(assetId);
            if (asset == null) {
                return null;
            }

            transactionContext.execute(() -> {
                executeQuery(connection, assetStatements.getDeleteAssetByIdTemplate(), assetId);
            });

            return asset;
        } catch (Exception e) {
            throw new EdcPersistenceException(e.getMessage(), e);
        }
    }

    @Override
    public DataAddress resolveForAsset(String assetId) {
        Objects.requireNonNull(assetId);

        try (var connection = getConnection()) {
            var dataAddressList = transactionContext.execute(() -> executeQuery(connection, this::mapDataAddress, assetStatements.getFindDataAddressByIdTemplate(), assetId));
            return single(dataAddressList);
        } catch (Exception e) {
            if (e instanceof EdcPersistenceException) {
                throw (EdcPersistenceException) e;
            } else {
                throw new EdcPersistenceException(e.getMessage(), e);
            }
        }
    }

    private int mapRowCount(ResultSet resultSet) throws SQLException {
        return resultSet.getInt(assetStatements.getCountVariableName());
    }

    private AbstractMap.SimpleImmutableEntry<String, Object> mapPropertyResultSet(ResultSet resultSet) throws SQLException, ClassNotFoundException, JsonProcessingException {
        var name = resultSet.getString(assetStatements.getAssetPropertyColumnName());
        var value = resultSet.getString(assetStatements.getAssetPropertyColumnValue());
        var type = resultSet.getString(assetStatements.getAssetPropertyColumnType());


        return new AbstractMap.SimpleImmutableEntry<>(name, fromPropertyValue(value, type));
    }

    @Nullable
    private <T> T single(List<T> list) {
        if (list.size() == 0) {
            return null;
        } else if (list.size() > 1) {
            throw new IllegalStateException("Expected result set size of 0 or 1 but got " + list.size());
        } else {
            return list.iterator().next();
        }
    }

    /**
     * Deserializes a value into an object using the object mapper. Note: if type is {@code java.lang.String} simply
     * {@code value.toString()} is returned.
     */
    private Object fromPropertyValue(String value, String type) throws ClassNotFoundException, JsonProcessingException {
        var clazz = Class.forName(type);
        if (clazz == String.class) {
            return value;
        }
        return objectMapper.readValue(value, clazz);
    }

    private boolean existsById(String assetId, Connection connection) {
        var assetCount = transactionContext.execute(() -> executeQuery(connection, this::mapRowCount, assetStatements.getCountAssetByIdClause(), assetId).iterator().next());

        if (assetCount <= 0) {
            return false;
        } else if (assetCount > 1) {
            throw new IllegalStateException("Expected result set size of 0 or 1 but got " + assetCount);
        } else {
            return true;
        }
    }

    private DataSource getDataSource() {
        return Objects.requireNonNull(dataSourceRegistry.resolve(dataSourceName), format("DataSource %s could not be resolved", dataSourceName));
    }

    private Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    private DataAddress mapDataAddress(ResultSet resultSet) throws SQLException, JsonProcessingException {
        return DataAddress.Builder.newInstance()
                .properties(objectMapper.readValue(resultSet.getString(assetStatements.getDataAddressColumnProperties()), new TypeReference<>() {
                }))
                .build();
    }

    private String mapAssetIds(ResultSet resultSet) throws SQLException {
        return resultSet.getString(assetStatements.getAssetIdColumn());
    }

    private String toPropertyValue(Object value) throws JsonProcessingException {
        return value instanceof String ? value.toString() : objectMapper.writeValueAsString(value);
    }
}
