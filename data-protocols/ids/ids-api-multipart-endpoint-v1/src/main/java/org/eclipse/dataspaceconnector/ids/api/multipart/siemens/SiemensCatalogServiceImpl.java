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

package org.eclipse.dataspaceconnector.ids.api.multipart.siemens;

import org.eclipse.dataspaceconnector.common.string.StringUtils;
import org.eclipse.dataspaceconnector.ids.spi.service.CatalogService;
import org.eclipse.dataspaceconnector.spi.contract.offer.ContractOfferQuery;
import org.eclipse.dataspaceconnector.spi.contract.offer.ContractOfferService;
import org.eclipse.dataspaceconnector.spi.iam.ClaimToken;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.domain.catalog.Catalog;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class SiemensCatalogServiceImpl implements CatalogService {
    private final Monitor monitor;
    private final String dataCatalogId;
    private final ContractOfferService contractOfferService;

    public SiemensCatalogServiceImpl(
            @NotNull Monitor monitor,
            @NotNull String dataCatalogId,
            @NotNull ContractOfferService contractOfferService) {
        this.monitor = Objects.requireNonNull(monitor);
        this.dataCatalogId = Objects.requireNonNull(dataCatalogId);
        this.contractOfferService = Objects.requireNonNull(contractOfferService);
    }

    /**
     * Provides the dataCatalog object, which may be used by the IDS self-description of the connector.
     *
     * @return data catalog
     */
    @Override
    @NotNull
    public Catalog getDataCatalog(ClaimToken claimToken) {
        var query = ContractOfferQuery.Builder.newInstance().claimToken(claimToken).build();

        var offers = contractOfferService.queryContractOffers(query).collect(toList());

        var ten = claimToken.getClaims().get("ten");

        if (Objects.isNull(ten)) {
            return Catalog.Builder.newInstance().id(dataCatalogId).contractOffers(offers).build();
        }

        return Catalog.Builder.newInstance().id(dataCatalogId)
                .contractOffers(offers.stream().filter(offer -> Objects.equals(offer.getAsset().getProperty("ten"), ten)).collect(Collectors.toUnmodifiableList()))
                .build();
    }
}
