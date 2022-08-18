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

package org.eclipse.dataspaceconnector.spi.event.policydefinition;

import org.eclipse.dataspaceconnector.spi.event.Event;
import org.eclipse.dataspaceconnector.spi.event.EventPayload;

import java.util.Objects;

/**
 * Describe a new PolicyDefinition creation, after this has emitted, a PolicyDefinition with a certain id will be available.
 */
public class PolicyDefinitionCreated extends Event<PolicyDefinitionCreated.Payload> {

    private PolicyDefinitionCreated() {
    }

    public static class Builder extends Event.Builder<PolicyDefinitionCreated, Payload, Builder> {

        public static Builder newInstance() {
            return new Builder();
        }

        private Builder() {
            super(new PolicyDefinitionCreated(), new Payload());
        }

        public Builder policyDefinitionId(String policyDefinitionId) {
            event.payload.policyDefinitionId = policyDefinitionId;
            return this;
        }

        @Override
        protected void validate() {
            Objects.requireNonNull(event.payload.policyDefinitionId);
        }
    }

    public static class Payload extends EventPayload {
        private String policyDefinitionId;

        public String getPolicyDefinitionId() {
            return policyDefinitionId;
        }
    }
}
