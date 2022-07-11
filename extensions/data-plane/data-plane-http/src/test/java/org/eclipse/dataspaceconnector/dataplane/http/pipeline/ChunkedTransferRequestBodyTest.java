/*
 *  Copyright (c) 2022 Amadeus
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Amadeus - Initial implementation
 *
 */

package org.eclipse.dataspaceconnector.dataplane.http.pipeline;

import com.github.javafaker.Faker;
import okio.BufferedSink;
import org.eclipse.dataspaceconnector.spi.types.domain.HttpDataAddress;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

<<<<<<<< HEAD:extensions/data-plane/data-plane-http/src/test/java/org/eclipse/dataspaceconnector/dataplane/http/pipeline/ChunckedRequestBodyTest.java
class ChunckedRequestBodyTest {
========
class ChunkedTransferRequestBodyTest {
>>>>>>>> bug/sink-aws-s3:extensions/data-plane/data-plane-http/src/test/java/org/eclipse/dataspaceconnector/dataplane/http/pipeline/ChunkedTransferRequestBodyTest.java
    private static final Faker FAKER = new Faker();

    @Test
    void verifyStreamingTransfer() throws IOException {
        var content = FAKER.lorem().word();
        var sink = mock(BufferedSink.class);
        var outputStream = new ByteArrayOutputStream();

        when(sink.outputStream()).thenReturn(outputStream);

<<<<<<<< HEAD:extensions/data-plane/data-plane-http/src/test/java/org/eclipse/dataspaceconnector/dataplane/http/pipeline/ChunckedRequestBodyTest.java
        var body = new ChunckedRequestBody(() -> new ByteArrayInputStream(content.getBytes()), HttpDataAddress.OCTET_STREAM);
========
        var body = new ChunkedTransferRequestBody(() -> new ByteArrayInputStream(content.getBytes()), HttpDataAddress.OCTET_STREAM);
>>>>>>>> bug/sink-aws-s3:extensions/data-plane/data-plane-http/src/test/java/org/eclipse/dataspaceconnector/dataplane/http/pipeline/ChunkedTransferRequestBodyTest.java
        body.writeTo(sink);

        assertThat(outputStream).hasToString(content);
    }
}
