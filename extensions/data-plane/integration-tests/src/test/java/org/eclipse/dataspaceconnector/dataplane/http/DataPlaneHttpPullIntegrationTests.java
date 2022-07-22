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
 *       Amadeus - initial API and implementation
 *       Mercedes Benz Tech Innovation - add toggles for proxy behavior
 *
 */

package org.eclipse.dataspaceconnector.dataplane.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.restassured.specification.RequestSpecification;
import net.datafaker.Faker;
import org.eclipse.dataspaceconnector.common.util.junit.annotations.ComponentTest;
import org.eclipse.dataspaceconnector.junit.extensions.EdcExtension;
import org.eclipse.dataspaceconnector.spi.system.ConfigurationExtension;
import org.eclipse.dataspaceconnector.spi.system.configuration.ConfigFactory;
import org.eclipse.dataspaceconnector.spi.types.domain.DataAddress;
import org.eclipse.dataspaceconnector.spi.types.domain.HttpDataAddress;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;
import org.mockserver.model.MediaType;
import org.mockserver.verify.VerificationTimes;

import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.dataspaceconnector.junit.testfixtures.TestUtils.getFreePort;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.matchers.Times.once;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.stop.Stop.stopQuietly;

/**
 * Integration tests for Data Plane public API querying to a http data source.
 */
@ExtendWith(EdcExtension.class)
@ComponentTest
public class DataPlaneHttpPullIntegrationTests {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Faker FAKER = new Faker();
    private static final String AUTH_HEADER_KEY = HttpHeaderNames.AUTHORIZATION.toString();

    // Validation API
    private static final int VALIDATION_API_PORT = getFreePort();
    private static final String VALIDATION_API_HOST = "http://localhost:" + VALIDATION_API_PORT;

    // DPF API
    private static final int DPF_CONTROL_API_PORT = getFreePort();
    private static final int DPF_PUBLIC_API_PORT = getFreePort();
    private static final String DPF_PUBLIC_API_HOST = "http://localhost:" + DPF_PUBLIC_API_PORT;
    private static final String DPF_PUBLIC_PATH = "/public";
    private static final String DPF_DATA_PATH = String.format("%s/", DPF_PUBLIC_PATH);

    // Data source API
    private static final int HTTP_SOURCE_API_PORT = getFreePort();
    private static final String HTTP_SOURCE_API_HOST = "http://localhost:" + HTTP_SOURCE_API_PORT;

    /**
     * HTTP Source mock server.
     */
    private static ClientAndServer httpSourceClientAndServer;

    /**
     * Validation API mock server.
     */
    private static ClientAndServer validationClientAndServer;

    private final Map<String, String> props = Map.of(
            "web.http.control.port", String.valueOf(DPF_CONTROL_API_PORT),
            "web.http.public.port", String.valueOf(DPF_PUBLIC_API_PORT),
            "web.http.public.path", DPF_PUBLIC_PATH,
            "edc.dataplane.token.validation.endpoint", VALIDATION_API_HOST
    );

    @BeforeAll
    public static void setUp() {
        httpSourceClientAndServer = startClientAndServer(HTTP_SOURCE_API_PORT);
        validationClientAndServer = startClientAndServer(VALIDATION_API_PORT);
    }

    @AfterAll
    public static void tearDown() {
        stopQuietly(httpSourceClientAndServer);
        stopQuietly(validationClientAndServer);
    }

    private static Stream<Arguments> providerTestInstances() {
        var get = new Scenario(HttpMethod.GET.name());
        var path = FAKER.lorem().word() + "/" + FAKER.lorem().word();
        var body = FAKER.lorem().sentence();

        var getWithQueryParams = new Scenario(HttpMethod.GET.name())
                .queryParam(FAKER.lorem().word(), FAKER.lorem().word())
                .queryParam(FAKER.lorem().word(), FAKER.lorem().word());

        var getWithPath = new Scenario(HttpMethod.GET.name())
                .path(path);

        var post = new Scenario(HttpMethod.POST.name())
                .requestBody(body);

        var postWithPath = new Scenario(HttpMethod.POST.name())
                .path(path)
                .requestBody(body);

        return Stream.of(
                Arguments.of("POST", post),
                Arguments.of("GET", get),
                Arguments.of("GET WITH QUERY PARAMS", getWithQueryParams),
                Arguments.of("GET WITH PATH", getWithPath),
                Arguments.of("POST WITH PATH", postWithPath)
        );
    }

    @BeforeEach
    void setProperties(EdcExtension extension) {
        extension.registerSystemExtension(ConfigurationExtension.class, (ConfigurationExtension) () -> ConfigFactory.fromMap(props));
    }

    /**
     * Reset mock server internal state after every test.
     */
    @AfterEach
    public void resetMockServer() {
        httpSourceClientAndServer.reset();
        validationClientAndServer.reset();
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("providerTestInstances")
    void test(String name, Scenario scenario) throws JsonProcessingException {
        // prepare server of the data source
        httpSourceClientAndServer.when(scenario.expectedSourceRequest, once())
                .respond(withResponse(HttpStatusCode.OK_200, OBJECT_MAPPER.writeValueAsString(scenario.sourceResponse)));

        // prepare validation server of the control plane
        var validationRequest = request().withMethod(HttpMethod.GET.name()).withHeader(AUTH_HEADER_KEY, scenario.token);
        validationClientAndServer.when(validationRequest, once())
                .respond(withResponse(HttpStatusCode.OK_200, OBJECT_MAPPER.writeValueAsString(scenario.sourceAddress)));

        scenario.dataplaneRequest.request(scenario.method).then().assertThat()
                .statusCode(200)
                .body("some", CoreMatchers.equalTo("info"));

        httpSourceClientAndServer.verify(scenario.expectedSourceRequest, VerificationTimes.once());
    }

    /**
     * Mock plain text response from source.
     *
     * @param statusCode   Response status code.
     * @param responseBody Response body.
     * @return see {@link HttpResponse}
     */
    private HttpResponse withResponse(HttpStatusCode statusCode, String responseBody) {
        var response = response()
                .withStatusCode(statusCode.code());

        if (responseBody != null) {
            response.withHeader(
                            new Header(HttpHeaderNames.CONTENT_TYPE.toString(),
                                    MediaType.APPLICATION_JSON.toString())
                    )
                    .withBody(responseBody);
        }

        return response;
    }

    private static final class Scenario {
        private final Map<String, String> sourceResponse = Map.of("some", "info");
        private final String method;
        private final String token;
        private final RequestSpecification dataplaneRequest;
        private final HttpRequest expectedSourceRequest;
        private final DataAddress sourceAddress;

        Scenario(String method) {
            this.method = method;
            token = FAKER.internet().uuid();
            dataplaneRequest = given()
                    .baseUri(DPF_PUBLIC_API_HOST)
                    .basePath(DPF_DATA_PATH)
                    .header(AUTH_HEADER_KEY, token);
            sourceAddress = HttpDataAddress.Builder.newInstance()
                    .baseUrl(HTTP_SOURCE_API_HOST)
                    .proxyBody(Boolean.TRUE.toString())
                    .proxyMethod(Boolean.TRUE.toString())
                    .proxyPath(Boolean.TRUE.toString())
                    .proxyQueryParams(Boolean.TRUE.toString())
                    .build();
            expectedSourceRequest = new HttpRequest();
        }

        public Scenario requestBody(String requestBody) {
            dataplaneRequest.body(requestBody);
            expectedSourceRequest.withBody(requestBody);
            return this;
        }

        public Scenario path(String path) {
            dataplaneRequest.basePath(DPF_DATA_PATH + path);
            expectedSourceRequest.withPath(path.startsWith("/") ? path : "/" + path);
            return this;
        }

        public Scenario queryParam(String key, String value) {
            dataplaneRequest.queryParam(key, value);
            expectedSourceRequest.withQueryStringParameter(key, value);
            return this;
        }
    }
}
