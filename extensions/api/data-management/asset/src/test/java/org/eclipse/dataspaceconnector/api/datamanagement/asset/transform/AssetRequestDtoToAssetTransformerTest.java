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
import org.eclipse.dataspaceconnector.spi.transformer.TransformerContext;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AssetRequestDtoToAssetTransformerTest {

    private final AssetRequestDtoToAssetTransformer transformer = new AssetRequestDtoToAssetTransformer();

    @Test
    void inputOutputType() {
        assertThat(transformer.getInputType()).isNotNull();
        assertThat(transformer.getOutputType()).isNotNull();
    }

    @Test
    void transform_id() {
        var context = mock(TransformerContext.class);
        var assetDto = AssetRequestDto.Builder.newInstance().id("assetId").properties(Map.of("key", "value")).build();

        var asset = transformer.transform(assetDto, context);

        assertThat(asset).isNotNull();
        assertThat(asset.getId()).isEqualTo("assetId");
        assertThat(asset.getProperties()).containsAllEntriesOf(assetDto.getProperties());
    }

    @Test
    void transform_nullId() {
        var context = mock(TransformerContext.class);
        var assetDto = AssetRequestDto.Builder.newInstance().properties(Map.of("key", "value")).build();

        var asset = transformer.transform(assetDto, context);

        assertThat(asset).isNotNull();
        assertThat(asset.getId()).isNotBlank();
        assertThat(asset.getProperties()).containsAllEntriesOf(assetDto.getProperties());
    }

    @Test
    void transform_idFromProperties() {
        var context = mock(TransformerContext.class);
        var assetDto = AssetRequestDto.Builder.newInstance().properties(Map.of(Asset.PROPERTY_ID, "assetId", "key", "value")).build();

        var asset = transformer.transform(assetDto, context);

        assertThat(asset).isNotNull();
        assertThat(asset.getId()).isEqualTo("assetId");
        assertThat(asset.getProperties()).containsExactlyInAnyOrderEntriesOf(assetDto.getProperties());
    }

    @Test
    void transform_nullInput() {
        var context = mock(TransformerContext.class);

        var asset = transformer.transform(null, context);

        assertThat(asset).isNull();
        verify(context).reportProblem("input asset is null");
    }
}