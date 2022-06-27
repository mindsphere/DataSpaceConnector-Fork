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
 *       Microsoft Corporation - initial API and implementation
 *       Siemens AG - changes to make it compatible with AWS S3, Azure blob and AWS China S3 presigned URL for upload
 *
 */

package org.eclipse.dataspaceconnector.dataplane.http.pipeline;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.eclipse.dataspaceconnector.common.string.StringUtils;
import org.eclipse.dataspaceconnector.dataplane.spi.pipeline.DataSink;
import org.eclipse.dataspaceconnector.dataplane.spi.pipeline.DataSinkFactory;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.result.Result;
import org.eclipse.dataspaceconnector.spi.types.domain.http.HttpDataAddressSchema;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataFlowRequest;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static org.eclipse.dataspaceconnector.spi.result.Result.failure;
import static org.eclipse.dataspaceconnector.spi.types.domain.http.HttpDataAddressSchema.ADDITIONAL_HEADERS;
import static org.eclipse.dataspaceconnector.spi.types.domain.http.HttpDataAddressSchema.AUTHENTICATION_CODE;
import static org.eclipse.dataspaceconnector.spi.types.domain.http.HttpDataAddressSchema.AUTHENTICATION_KEY;
import static org.eclipse.dataspaceconnector.spi.types.domain.http.HttpDataAddressSchema.ENDPOINT;
import static org.eclipse.dataspaceconnector.spi.types.domain.http.HttpDataAddressSchema.HTTP_VERB;
import static org.eclipse.dataspaceconnector.spi.types.domain.http.HttpDataAddressSchema.TYPE;
import static org.eclipse.dataspaceconnector.spi.types.domain.http.HttpDataAddressSchema.USE_PART_NAME;

/**
 * Instantiates {@link HttpDataSink}s for requests whose source data type is {@link HttpDataAddressSchema#TYPE}.
 */
public class HttpDataSinkFactory implements DataSinkFactory {
    private final OkHttpClient httpClient;
    private final ExecutorService executorService;
    private final int partitionSize;
    private final Monitor monitor;
    private final ObjectMapper mapper;

    public HttpDataSinkFactory(OkHttpClient httpClient, ObjectMapper mapper, ExecutorService executorService, int partitionSize, Monitor monitor) {
        this.httpClient = httpClient;
        this.executorService = executorService;
        this.partitionSize = partitionSize;
        this.monitor = monitor;
        this.mapper = mapper;
    }

    @Override
    public boolean canHandle(DataFlowRequest request) {
        return TYPE.equals(request.getDestinationDataAddress().getType());
    }

    @Override
    public @NotNull Result<Boolean> validate(DataFlowRequest request) {
        var dataAddress = request.getDestinationDataAddress();
        if (dataAddress == null || !dataAddress.getProperties().containsKey(ENDPOINT)) {
            return failure("HTTP data sink endpoint not provided for request: " + request.getId());
        }
        return VALID;
    }

    @Override
    public DataSink createSink(DataFlowRequest request) {
        var dataAddress = request.getDestinationDataAddress();
        var requestId = request.getId();
        var endpoint = dataAddress.getProperty(ENDPOINT);
        if (endpoint == null) {
            throw new EdcException("HTTP data destination endpoint not provided for request: " + requestId);
        }
        var authKey = dataAddress.getProperty(AUTHENTICATION_KEY);
        var authCode = dataAddress.getProperty(AUTHENTICATION_CODE);
        var httpVerb = dataAddress.getProperty(HTTP_VERB, "POST");
        var usePartName = Boolean.parseBoolean(dataAddress.getProperty(USE_PART_NAME, Boolean.TRUE.toString()));
        var additionalHeaders = dataAddress.getProperty(ADDITIONAL_HEADERS, "{}");

        return HttpDataSink.Builder.newInstance()
                .endpoint(endpoint)
                .requestId(requestId)
                .partitionSize(partitionSize)
                .authKey(authKey)
                .authCode(authCode)
                .usePartName(usePartName)
                .httpVerb(httpVerb)
                .additionalHeaders(convertAdditionalHeaders(additionalHeaders))
                .httpClient(httpClient)
                .executorService(executorService)
                .monitor(monitor)
                .build();
    }

    private Map<String, String> convertAdditionalHeaders(String additionalHeaders) {
        try {
            return mapper.readValue(StringUtils.isNullOrBlank(additionalHeaders) ? "" : additionalHeaders, Map.class);
        } catch (JsonProcessingException e) {
            monitor.severe("Failed to set additional headers from " + additionalHeaders, e);
        }

        return new HashMap<>();
    }
}
