/*
 *  Copyright (c) 2021 Daimler TSS GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Daimler TSS GmbH - Initial API and Implementation
 *
 */

package org.eclipse.dataspaceconnector.ids.core.service;

import org.eclipse.dataspaceconnector.ids.spi.domain.connector.SecurityProfile;
import org.eclipse.dataspaceconnector.ids.spi.types.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.types.IdsType;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConnectorServiceSettings {

    @EdcSetting
    public static final String EDC_IDS_ID = "edc.ids.id";
    @EdcSetting
    public static final String EDC_IDS_TITLE = "edc.ids.title";
    @EdcSetting
    public static final String EDC_IDS_DESCRIPTION = "edc.ids.description";
    @EdcSetting
    public static final String EDC_IDS_SECURITY_PROFILE = "edc.ids.security.profile";
    @EdcSetting
    public static final String EDC_IDS_ENDPOINT = "edc.ids.endpoint";
    @EdcSetting
    public static final String EDC_IDS_MAINTAINER = "edc.ids.maintainer";
    @EdcSetting
    public static final String EDC_IDS_CURATOR = "edc.ids.curator";

    public static final String DEFAULT_EDC_IDS_TITLE = "Eclipse Dataspace Connector";
    public static final String DEFAULT_EDC_IDS_ID = "urn:connector:edc";
    public static final String DEFAULT_EDC_IDS_DESCRIPTION = "Eclipse Dataspace Connector";
    public static final String DEFAULT_EDC_IDS_SECURITY_PROFILE = SecurityProfile.BASE_SECURITY_PROFILE.getValue();
    public static final String DEFAULT_EDC_IDS_ENDPOINT = "https://example.com";
    public static final String DEFAULT_EDC_IDS_MAINTAINER = "https://example.com";
    public static final String DEFAULT_EDC_IDS_CURATOR = "https://example.com";

    private static final String WARNING_USING_DEFAULT_SETTING = "IDS Settings: No setting found for key '%s'. Using default value '%s'";
    private static final String ERROR_INVALID_SETTING = "IDS Settings: Invalid setting for '%s'. Was %s'.";

    private final Monitor monitor;
    private final ServiceExtensionContext serviceExtensionContext;

    // TODO Shouldn't the connector ID be an URI? Ask Denis
    private String id;
    private String title;
    private String description;
    private SecurityProfile securityProfile;
    private URI endpoint;
    private URI maintainer;
    private URI curator;

    public ConnectorServiceSettings(@NotNull ServiceExtensionContext serviceExtensionContext, @NotNull Monitor monitor) {
        this.serviceExtensionContext = Objects.requireNonNull(serviceExtensionContext);
        this.monitor = Objects.requireNonNull(monitor);

        List<String> errors = new ArrayList<>();

        initTitle();
        initDescription();
        var error = setConnectorId();
        if (error != null) {
            errors.add(error);
        }

        error = initSecurityProfile();
        if (error != null) {
            errors.add(error);
        }

        error = initEndpoint();
        if (error != null) {
            errors.add(error);
        }

        error = initMaintainer();
        if (error != null) {
            errors.add(error);
        }

        error = initCurator();
        if (error != null) {
            errors.add(error);
        }

        if (!errors.isEmpty()) {
            throw new EdcException(String.join(", ", errors));
        }
    }

    @Nullable
    public String getId() {
        return id;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Nullable
    public SecurityProfile getSecurityProfile() {
        return securityProfile;
    }

    @Nullable
    public URI getEndpoint() {
        return endpoint;
    }

    @Nullable
    public URI getMaintainer() {
        return maintainer;
    }

    @Nullable
    public URI getCurator() {
        return curator;
    }

    private String setConnectorId() {
        String value = serviceExtensionContext.getSetting(EDC_IDS_ID, null);

        if (value == null) {
            String message = "IDS Settings: No setting found for key '%s'. Using default value '%s'";
            monitor.warning(String.format(message, EDC_IDS_ID, DEFAULT_EDC_IDS_ID));
            value = DEFAULT_EDC_IDS_ID;
        }

        // Hint: use stringified uri to keep uri path and query
        var result = IdsId.from(value);
        if (result.succeeded()) {
            var idsId = result.getContent();
            if (idsId.getType() == IdsType.CONNECTOR) {
                id = idsId.getValue();
                return null;
            }
        }

        return String.format(ERROR_INVALID_SETTING, EDC_IDS_ID, value);
    }

    private void initTitle() {
        String value = serviceExtensionContext.getSetting(EDC_IDS_TITLE, null);

        if (value == null) {
            monitor.warning(String.format(WARNING_USING_DEFAULT_SETTING, EDC_IDS_TITLE, DEFAULT_EDC_IDS_TITLE));
            value = DEFAULT_EDC_IDS_TITLE;
        }

        this.title = value;
    }

    private void initDescription() {
        String value = serviceExtensionContext.getSetting(EDC_IDS_DESCRIPTION, null);

        if (value == null) {
            monitor.warning(String.format(WARNING_USING_DEFAULT_SETTING, EDC_IDS_DESCRIPTION, DEFAULT_EDC_IDS_DESCRIPTION));
            value = DEFAULT_EDC_IDS_DESCRIPTION;
        }

        this.description = value;
    }

    private String initSecurityProfile() {
        String value = serviceExtensionContext.getSetting(EDC_IDS_SECURITY_PROFILE, null);

        if (value == null) {
            monitor.warning(String.format(WARNING_USING_DEFAULT_SETTING, EDC_IDS_SECURITY_PROFILE, DEFAULT_EDC_IDS_SECURITY_PROFILE));
            value = DEFAULT_EDC_IDS_SECURITY_PROFILE;
        }

        this.description = value;

        try {
            this.securityProfile = SecurityProfile.fromValue(value);
        } catch (IllegalArgumentException illegalArgumentException) {
            return String.format(ERROR_INVALID_SETTING, EDC_IDS_SECURITY_PROFILE, value);
        }
        return null;
    }

    private String initEndpoint() {
        String value = serviceExtensionContext.getSetting(EDC_IDS_ENDPOINT, null);

        if (value == null) {
            monitor.warning(String.format(WARNING_USING_DEFAULT_SETTING, EDC_IDS_ENDPOINT, DEFAULT_EDC_IDS_ENDPOINT));
            value = DEFAULT_EDC_IDS_ENDPOINT;
        }

        try {
            endpoint = URI.create(value);
        } catch (IllegalArgumentException e) {
            return String.format(ERROR_INVALID_SETTING, EDC_IDS_ENDPOINT, value);
        }

        return null;
    }

    private String initMaintainer() {
        String value = serviceExtensionContext.getSetting(EDC_IDS_MAINTAINER, null);

        if (value == null) {
            monitor.warning(String.format(WARNING_USING_DEFAULT_SETTING, EDC_IDS_MAINTAINER, DEFAULT_EDC_IDS_MAINTAINER));
            value = DEFAULT_EDC_IDS_MAINTAINER;
        }

        try {
            maintainer = URI.create(value);
        } catch (IllegalArgumentException e) {
            return String.format(ERROR_INVALID_SETTING, EDC_IDS_MAINTAINER, value);
        }

        return null;
    }

    private String initCurator() {
        String value = serviceExtensionContext.getSetting(EDC_IDS_CURATOR, null);

        if (value == null) {
            monitor.warning(String.format(WARNING_USING_DEFAULT_SETTING, EDC_IDS_CURATOR, DEFAULT_EDC_IDS_CURATOR));
            value = DEFAULT_EDC_IDS_CURATOR;
        }

        try {
            curator = URI.create(value);
        } catch (IllegalArgumentException e) {
            return String.format(ERROR_INVALID_SETTING, EDC_IDS_CURATOR, value);
        }

        return null;
    }
}
