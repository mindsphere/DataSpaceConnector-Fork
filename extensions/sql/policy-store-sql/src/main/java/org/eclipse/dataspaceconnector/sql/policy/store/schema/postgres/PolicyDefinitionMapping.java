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

package org.eclipse.dataspaceconnector.sql.policy.store.schema.postgres;

import org.eclipse.dataspaceconnector.sql.policy.store.schema.SqlPolicyStoreStatements;
import org.eclipse.dataspaceconnector.sql.translation.TranslationMapping;

/**
 * Maps fields of a {@link org.eclipse.dataspaceconnector.policy.model.PolicyDefinition} onto the corresponding SQL
 * schema (= column names) enabling access through Postgres JSON operators where applicable
 */
public class PolicyDefinitionMapping extends TranslationMapping {
    public PolicyDefinitionMapping(SqlPolicyStoreStatements statements) {
        add("uid", statements.getPolicyIdColumn());
        add("policy", new PolicyMapping(statements));
    }
}
