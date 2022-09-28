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

package org.eclipse.dataspaceconnector.spi.event.transferprocess;

import org.eclipse.dataspaceconnector.spi.event.Event;

import java.util.Objects;

/**
 * This event is raised when the TransferProcess has been initiated.
 */
public class TransferProcessInitiated extends Event<TransferProcessInitiated.Payload> {

    private TransferProcessInitiated() {
    }

    public static class Builder extends Event.Builder<TransferProcessInitiated, Payload, Builder> {

        public static Builder newInstance() {
            return new Builder();
        }

        private Builder() {
            super(new TransferProcessInitiated(), new Payload());
        }

        public Builder transferProcessId(String transferProcessId) {
            event.payload.transferProcessId = transferProcessId;
            return this;
        }

        @Override
        protected void validate() {
            Objects.requireNonNull(event.payload.transferProcessId);
        }
    }

    public static class Payload extends TransferProcessEventPayload {
    }
}
