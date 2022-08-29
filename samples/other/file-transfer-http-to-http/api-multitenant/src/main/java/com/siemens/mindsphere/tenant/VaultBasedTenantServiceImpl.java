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
 *
 */

package com.siemens.mindsphere.tenant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.eclipse.dataspaceconnector.common.string.StringUtils;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.security.CertificateResolver;
import org.eclipse.dataspaceconnector.spi.security.PrivateKeyResolver;
import org.eclipse.dataspaceconnector.spi.security.Vault;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;

public class VaultBasedTenantServiceImpl implements TenantService {

    private static final String EMPTY_TENANT = "";

    private final Vault vault;
    private final PrivateKeyResolver privateKeyResolver;
    private final CertificateResolver certificateResolver;
    private final ObjectMapper objectMapper;
    private final ServiceExtensionContext context;

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class TenantInfo {
        @JsonProperty("bpn")
        private String bpn;

        @JsonProperty("client_id")
        private String clientId;

        TenantInfo() {
        }

        public String getBpn() {
            return bpn;
        }

        public void setBpn(String bpn) {
            this.bpn = bpn;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }
    }

    public VaultBasedTenantServiceImpl(Vault vault, PrivateKeyResolver privateKeyResolver, CertificateResolver certificateResolver, ServiceExtensionContext context) {
        this(vault, privateKeyResolver, certificateResolver, context, new ObjectMapper());
    }

    public VaultBasedTenantServiceImpl(Vault vault, PrivateKeyResolver privateKeyResolver, CertificateResolver certificateResolver, ServiceExtensionContext context, ObjectMapper objectMapper) {
        this.vault = vault;
        this.privateKeyResolver = privateKeyResolver;
        this.certificateResolver = certificateResolver;
        this.objectMapper = objectMapper;
        this.context = context;
    }

    @Override
    public String bpnFromTenantName(String tenantName) {
        if (StringUtils.isNullOrBlank(tenantName)) {
            return EMPTY_TENANT;
        }

        var secret = vault.resolveSecret("tenantinfo_" + tenantName);
        if (StringUtils.isNullOrBlank(secret)) {
            return EMPTY_TENANT;
        }

        try {
            return objectMapper.readValue(secret, TenantInfo.class).bpn;
        } catch (JsonProcessingException e) {
            context.getMonitor().severe("Failed to read tenant info " + tenantName, e);
        }

        return EMPTY_TENANT;
    }

    public String clientIdFromTenantName(String tenantName) {
        if (StringUtils.isNullOrBlank(tenantName)) {
            return EMPTY_TENANT;
        }

        var secret = vault.resolveSecret("tenantinfo_" + tenantName);
        if (StringUtils.isNullOrBlank(secret)) {
            return EMPTY_TENANT;
        }

        try {
            return objectMapper.readValue(secret, TenantInfo.class).clientId;
        } catch (JsonProcessingException e) {
            context.getMonitor().severe("Failed to read tenant info " + tenantName, e);
        }

        return EMPTY_TENANT;
    }

    @Override
    public PrivateKey getCurrentTenantPrivateKey() {
        return privateKeyResolver.resolvePrivateKey(bpnFromTenantName(TLS_TENANT.get()), PrivateKey.class);
    }

    @Override
    public byte[] getCurrentTenantEncodedClientCertificate() {
        var certificateName = bpnFromTenantName(TLS_TENANT.get()) + "-public";
        context.getMonitor().debug(() -> "Reading certificate name " + certificateName);

        var certificate = certificateResolver.resolveCertificate(certificateName);
        if (certificate == null) {
            throw new EdcException("Public certificate not found: " + certificateName);
        }

        try {
            return certificate.getEncoded();
        } catch (CertificateEncodingException e) {
            throw new EdcException("Failed to encode certificate: " + e);
        }
    }

    @Override
    public String getCurrentTenantClientId() {
        var clientId = clientIdFromTenantName(TLS_TENANT.get());
        context.getMonitor().debug(() -> "Current client is id is " + clientId);
        return clientId;
    }
}
