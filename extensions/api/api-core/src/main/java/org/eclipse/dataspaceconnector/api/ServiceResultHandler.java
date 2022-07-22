/*
 *  Copyright (c) 2022 Microsoft Corporation
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

package org.eclipse.dataspaceconnector.api;

import org.eclipse.dataspaceconnector.api.result.ServiceResult;
import org.eclipse.dataspaceconnector.spi.EdcException;
import org.eclipse.dataspaceconnector.spi.exception.ObjectExistsException;
import org.eclipse.dataspaceconnector.spi.exception.ObjectNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServiceResultHandler {

    /**
     * Interprets a {@link ServiceResult} based on its {@link ServiceResult#reason()} property and returns the
     * appropriate exception:
     * <table>
     *   <tr>
     *     <th>reason</th> <th>exception</th>
     *   </tr>
     *   <tr>
     *     <td>NOT_FOUND </td> <td>ObjectNotFoundException</td>
     *   </tr>
     *   <tr>
     *     <td>CONFLICT</td> <td>ObjectExistsException</td>
     *   </tr>
     *   <tr>
     *     <td>BAD_REQUEST</td> <td>IllegalArgumentException</td>
     *   </tr>
     *   <tr>
     *     <td>other</td> <td>EdcException</td>
     *   </tr>
     *   <caption>Mapping from failure reason to exception</caption>
     * </table>
     *
     * @param result The {@link ServiceResult}
     * @param clazz The type in whose context the failure occurred. Must not be null.
     * @param id The id of the entity which was involved in the failure. Can be null for
     *         {@link org.eclipse.dataspaceconnector.api.result.ServiceFailure.Reason#BAD_REQUEST}.
     * @return Exception mapped from failure reason.
     */
    public static RuntimeException mapToException(@NotNull ServiceResult<?> result, @NotNull Class<?> clazz, @Nullable String id) {
        switch (result.reason()) {
            case NOT_FOUND:
                return new ObjectNotFoundException(clazz, id);
            case CONFLICT:
                return new ObjectExistsException(clazz, id);
            case BAD_REQUEST:
                return new IllegalArgumentException(result.getFailureDetail());
            default:
                return new EdcException("unexpected error");
        }
    }
}
