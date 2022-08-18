/*
 *  Copyright (c) 2022 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

package org.eclipse.dataspaceconnector.api.datamanagement.asset.transform;

import org.eclipse.dataspaceconnector.api.datamanagement.asset.model.AssetRequestDto;
import org.eclipse.dataspaceconnector.api.transformer.DtoTransformer;
import org.eclipse.dataspaceconnector.spi.transformer.TransformerContext;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AssetRequestDtoToAssetTransformer implements DtoTransformer<AssetRequestDto, Asset> {

    @Override
    public Class<AssetRequestDto> getInputType() {
        return AssetRequestDto.class;
    }

    @Override
    public Class<Asset> getOutputType() {
        return Asset.class;
    }

    @Override
    public @Nullable Asset transform(@Nullable AssetRequestDto object, @NotNull TransformerContext context) {
        return Optional.ofNullable(object)
                .map(input -> Asset.Builder.newInstance()
                        .id(input.getId())
                        .properties(input.getProperties())
                        .build()
                )
                .orElseGet(() -> {
                    context.reportProblem("input asset is null");
                    return null;
                });

    }
}
