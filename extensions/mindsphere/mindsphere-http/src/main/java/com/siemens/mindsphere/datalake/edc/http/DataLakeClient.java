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

public interface DataLakeClient {
    URL getPresignedUploadUrl(String path) throws DataLakeException;

    URL getPresignedDownloadUrl(String datalakePath) throws DataLakeException;

    boolean isPresent(String path) throws DataLakeException;
}
