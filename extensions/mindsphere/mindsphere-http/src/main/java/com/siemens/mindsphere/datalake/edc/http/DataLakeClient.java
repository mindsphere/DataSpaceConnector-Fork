package com.siemens.mindsphere.datalake.edc.http;

import java.net.URL;

public interface DataLakeClient {
    URL getUrl(String path);
}
