/*
 *  Copyright (c) 2021 - 2022 Daimler TSS GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Daimler TSS GmbH - Initial API and Implementation
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - improvements
 *
 */

package com.siemens.mindsphere.tenant;

import org.eclipse.dataspaceconnector.ids.spi.service.CatalogService;
import org.eclipse.dataspaceconnector.spi.contract.offer.ContractOfferQuery;
import org.eclipse.dataspaceconnector.spi.contract.offer.ContractOfferService;
import org.eclipse.dataspaceconnector.spi.iam.ClaimToken;
import org.eclipse.dataspaceconnector.spi.message.Range;
import org.eclipse.dataspaceconnector.spi.types.domain.catalog.Catalog;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class SiemensCatalogServiceImpl implements CatalogService {
    private final String dataCatalogId;
    private final ContractOfferService contractOfferService;
    private final TenantService tenantService;

    public SiemensCatalogServiceImpl(
            @NotNull String dataCatalogId,
            @NotNull ContractOfferService contractOfferService,
            @NotNull TenantService tenantService) {
        this.dataCatalogId = Objects.requireNonNull(dataCatalogId);
        this.contractOfferService = Objects.requireNonNull(contractOfferService);
        this.tenantService = Objects.requireNonNull(tenantService);
    }

    /**
     * Provides the dataCatalog object, which may be used by the IDS self-description of the connector.
     *
     * @return data catalog
     */
    @Override
    @NotNull
    public Catalog getDataCatalog(ClaimToken claimToken, Range range) {
        var query = ContractOfferQuery.Builder.newInstance().claimToken(claimToken).build();

        var offers = contractOfferService.queryContractOffers(query, range).collect(toList());
        var ten = getTenant(claimToken.getClaims().get("client_id"));

        if (Objects.isNull(ten)) {
            return Catalog.Builder.newInstance().id(dataCatalogId).contractOffers(offers).build();
        }

        return Catalog.Builder.newInstance().id(dataCatalogId)
                .contractOffers(offers.stream().filter(offer -> Objects.equals(offer.getAsset().getProperty("tenant"), ten)).collect(Collectors.toUnmodifiableList()))
                .build();
    }

    private String getTenant(Object clientId) {
        return tenantService.tenantFromClientId(clientId);
    }
}