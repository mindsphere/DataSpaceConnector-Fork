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
 *       Daimler TSS GmbH - Initial Implementation
 *
 */

package org.eclipse.dataspaceconnector.ids.transform;

import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTransformerRegistry;
import org.eclipse.dataspaceconnector.ids.transform.type.asset.AssetFromIdsArtifactTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.asset.AssetFromIdsRepresentationTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.asset.AssetFromIdsResourceTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.asset.AssetToIdsArtifactTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.asset.AssetToIdsRepresentationTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.asset.AssetToIdsResourceTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.asset.OfferedAssetToIdsResourceTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.connector.CatalogFromIdsResourceCatalogTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.connector.CatalogToIdsResourceCatalogTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.connector.ConnectorToIdsConnectorTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.connector.SecurityProfileToIdsSecurityProfileTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.contract.ContractAgreementFromIdsContractAgreementTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.contract.ContractAgreementToIdsContractAgreementTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.contract.ContractOfferFromIdsContractOfferOrRequestTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.contract.ContractOfferToIdsContractOfferTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.policy.ActionToIdsActionTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.policy.ConstraintFromIdsConstraintTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.policy.ConstraintFromIdsLogicalConstraintTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.policy.ConstraintToIdsConstraintTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.policy.ConstraintToIdsLogicalConstraintTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.policy.DutyToIdsDutyTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.policy.ExpressionFromIdsLeftOperandTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.policy.ExpressionFromIdsRdfResourceTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.policy.ExpressionToIdsLeftOperandTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.policy.ExpressionToIdsRdfResourceTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.policy.OperatorFromIdsBinaryOperatorTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.policy.OperatorToIdsBinaryOperatorTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.policy.PermissionFromIdsPermissionTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.policy.PermissionToIdsPermissionTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.policy.ProhibitionFromIdsProhibitionTransformer;
import org.eclipse.dataspaceconnector.ids.transform.type.policy.ProhibitionToIdsProhibitionTransformer;
import org.eclipse.dataspaceconnector.spi.system.Inject;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

import java.util.Arrays;

public class IdsTransformServiceExtension implements ServiceExtension {

    @Inject
    private IdsTransformerRegistry registry;

    @Override
    public String name() {
        return "IDS Transform Extension";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        Arrays.asList(
                new ActionToIdsActionTransformer(),
                new AssetToIdsArtifactTransformer(),
                new AssetToIdsRepresentationTransformer(),
                new AssetToIdsResourceTransformer(),
                new ConnectorToIdsConnectorTransformer(),
                new ConstraintToIdsConstraintTransformer(),
                new ConstraintToIdsLogicalConstraintTransformer(),
                new ContractOfferToIdsContractOfferTransformer(),
                new ContractAgreementToIdsContractAgreementTransformer(),
                new CatalogToIdsResourceCatalogTransformer(),
                new DutyToIdsDutyTransformer(),
                new ExpressionToIdsLeftOperandTransformer(),
                new ExpressionToIdsRdfResourceTransformer(),
                new AssetFromIdsArtifactTransformer(),
                new OperatorFromIdsBinaryOperatorTransformer(),
                new ConstraintFromIdsConstraintTransformer(),
                new ConstraintFromIdsLogicalConstraintTransformer(),
                new ContractAgreementFromIdsContractAgreementTransformer(),
                new ContractOfferFromIdsContractOfferOrRequestTransformer(),
                new ExpressionFromIdsLeftOperandTransformer(),
                new PermissionFromIdsPermissionTransformer(),
                new ProhibitionFromIdsProhibitionTransformer(),
                new ExpressionFromIdsRdfResourceTransformer(),
                new AssetFromIdsRepresentationTransformer(),
                new AssetFromIdsResourceTransformer(),
                new CatalogFromIdsResourceCatalogTransformer(),
                new OfferedAssetToIdsResourceTransformer(),
                new OperatorToIdsBinaryOperatorTransformer(),
                new PermissionToIdsPermissionTransformer(),
                new ProhibitionToIdsProhibitionTransformer(),
                new SecurityProfileToIdsSecurityProfileTransformer()
        ).forEach(registry::register);
    }

}
