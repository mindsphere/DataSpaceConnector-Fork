/*
 *  Copyright (c) 2021 Microsoft Corporation
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

package org.eclipse.dataspaceconnector.policy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PolicyDefinitionTest {
    @Test
    void deserialize() throws JsonProcessingException {
        var policyJson = "{\"uid\":\"uid1\",\"policy\":{\"permissions\":[{\"edctype\":\"dataspaceconnector:permission\",\"uid\":\"permissionUid\"," +
                "\"constraints\":[],\"duties\":[]}],\"prohibitions\":[],\"obligations\":[],\"extensibleProperties\":{}," +
                "\"target\":\"assetId\",\"@type\":{\"@policytype\":\"contract\"}}}";
        var mapper = new ObjectMapper();

        var policyDefinition =  mapper.readValue(policyJson, PolicyDefinition.class);

        assertThat(policyDefinition.getUid()).hasToString("uid1");
        assertThat(policyDefinition.getPolicy().getType()).hasToString("CONTRACT");
    }

    @Test
    void serialize() throws JsonProcessingException {
        var policyDefinition = PolicyDefinition.Builder.newInstance()
                .uid("uid1")
                .policy(Policy.Builder.newInstance()
                    .target("assetId")
                        .type(PolicyType.CONTRACT)
                        .permission(
                                Permission.Builder.newInstance()
                                    .uid("permissionUid")
                                    .build())
                        .build())
                .build();

        var mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        var policyJson =  mapper.writeValueAsString(policyDefinition);

        assertThat(policyJson).hasToString("{\"uid\":\"uid1\",\"policy\":{\"permissions\":[{\"edctype\":\"dataspaceconnector:permission\"," +
                "\"uid\":\"permissionUid\",\"constraints\":[],\"duties\":[]}],\"prohibitions\":[],\"obligations\":[]," +
                "\"extensibleProperties\":{},\"target\":\"assetId\",\"@type\":{\"@policytype\":\"contract\"}}}");
    }
}