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

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class OauthClientDetails {
    public OauthClientDetails(String clientId, String clientSecret, String clientAppName, String clientAppVersion, String tenant, URL accessTokenUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.clientAppName = clientAppName;
        this.clientAppVersion = clientAppVersion;
        this.tenant = tenant;
        this.accessTokenUrl = accessTokenUrl;
    }

    private String clientId;

    private String clientSecret;

    private String clientAppName;

    private String clientAppVersion;

    private String tenant;

    private URL accessTokenUrl;

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getClientAppName() {
        return clientAppName;
    }

    public String getClientAppVersion() {
        return clientAppVersion;
    }

    public String getTenant() {
        return tenant;
    }

    public URL getAccessTokenUrl() {
        return accessTokenUrl;
    }

    public String getBase64Credentials() {
        return Base64.getEncoder().encodeToString((this.clientId + ":" + this.clientSecret).getBytes(StandardCharsets.UTF_8));
    }
}
