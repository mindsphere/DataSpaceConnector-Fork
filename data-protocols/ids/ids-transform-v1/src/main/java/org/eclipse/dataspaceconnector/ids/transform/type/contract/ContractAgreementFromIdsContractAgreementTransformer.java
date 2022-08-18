/*
 *  Copyright (c) 2021 - 2022 Fraunhofer Institute for Software and Systems Engineering
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Fraunhofer Institute for Software and Systems Engineering - Initial Implementation
 *       Daimler TSS GmbH - fixed contract dates to epoch seconds
 *       Fraunhofer Institute for Software and Systems Engineering - refactoring
 *
 */

package org.eclipse.dataspaceconnector.ids.transform.type.contract;

import de.fraunhofer.iais.eis.Contract;
import org.eclipse.dataspaceconnector.ids.spi.transform.ContractAgreementTransformerOutput;
import org.eclipse.dataspaceconnector.ids.spi.transform.ContractTransformerInput;
import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.dataspaceconnector.ids.spi.types.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.types.IdsType;
import org.eclipse.dataspaceconnector.policy.model.Duty;
import org.eclipse.dataspaceconnector.policy.model.Permission;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.eclipse.dataspaceconnector.policy.model.Prohibition;
import org.eclipse.dataspaceconnector.spi.transformer.TransformerContext;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.agreement.ContractAgreement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.eclipse.dataspaceconnector.ids.transform.type.contract.PropertyUtil.addIdsContractPropertiesToPolicy;

/**
 * Transforms an IDS ContractAgreement into an {@link ContractAgreement}.
 */
public class ContractAgreementFromIdsContractAgreementTransformer implements IdsTypeTransformer<ContractTransformerInput, ContractAgreementTransformerOutput> {

    @Override
    public Class<ContractTransformerInput> getInputType() {
        return ContractTransformerInput.class;
    }

    @Override
    public Class<ContractAgreementTransformerOutput> getOutputType() {
        return ContractAgreementTransformerOutput.class;
    }

    @Override
    public @Nullable ContractAgreementTransformerOutput transform(ContractTransformerInput object, @NotNull TransformerContext context) {
        Objects.requireNonNull(context);
        if (object == null) {
            return null;
        }

        var contractAgreement = (de.fraunhofer.iais.eis.ContractAgreement) object.getContract();
        var asset = object.getAsset();

        var edcPermissions = Optional.of(contractAgreement)
                .map(Contract::getPermission)
                .stream().flatMap(Collection::stream)
                .map(it -> context.transform(it, Permission.class))
                .collect(Collectors.toList());

        var edcProhibitions = Optional.of(contractAgreement)
                .map(Contract::getProhibition)
                .stream().flatMap(Collection::stream)
                .map(it -> context.transform(it, Prohibition.class))
                .collect(Collectors.toList());

        var edcObligations = Optional.of(contractAgreement)
                .map(Contract::getObligation)
                .stream().flatMap(Collection::stream)
                .map(it -> context.transform(it, Duty.class))
                .collect(Collectors.toList());

        var policyBuilder = Policy.Builder.newInstance();

        policyBuilder.duties(edcObligations);
        policyBuilder.prohibitions(edcProhibitions);
        policyBuilder.permissions(edcPermissions);

        var policy = addIdsContractPropertiesToPolicy(contractAgreement.getProperties(), policyBuilder).build();

        var builder = ContractAgreement.Builder.newInstance()
                .policy(policy)
                .consumerAgentId(String.valueOf(contractAgreement.getConsumer()))
                .providerAgentId(String.valueOf(contractAgreement.getProvider()))
                .assetId(asset.getId());

        var result = IdsId.from(contractAgreement.getId());
        if (result.failed()) {
            context.reportProblem("id of incoming IDS contract agreement expected to be not null");
            // contract agreement builder requires id to be not null
            return null;
        }

        var idsId = result.getContent();
        if (!idsId.getType().equals(IdsType.CONTRACT_AGREEMENT)) {
            context.reportProblem("handled object is not of type IDS contract agreement");
        }

        builder.id(idsId.getValue());

        if (contractAgreement.getContractEnd() != null) {
            builder.contractEndDate(contractAgreement.getContractEnd().toGregorianCalendar().toZonedDateTime().toEpochSecond());
        }

        if (contractAgreement.getContractStart() != null) {
            builder.contractStartDate(contractAgreement.getContractStart().toGregorianCalendar().toZonedDateTime().toEpochSecond());
        }

        if (contractAgreement.getContractDate() != null) {
            builder.contractSigningDate(contractAgreement.getContractDate().toGregorianCalendar().toZonedDateTime().toEpochSecond());
        }

        return ContractAgreementTransformerOutput.Builder.newInstance()
                .contractAgreement(builder.build())
                .policy(policy)
                .build();
    }
}
