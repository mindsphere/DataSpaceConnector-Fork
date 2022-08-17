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
 *       Fraunhofer Institute for Software and Systems Engineering - refactoring
 *
 */

package org.eclipse.dataspaceconnector.ids.transform.type.policy;

import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.dataspaceconnector.ids.spi.types.IdsId;
import org.eclipse.dataspaceconnector.policy.model.Action;
import org.eclipse.dataspaceconnector.policy.model.Constraint;
import org.eclipse.dataspaceconnector.policy.model.Duty;
import org.eclipse.dataspaceconnector.policy.model.Permission;
import org.eclipse.dataspaceconnector.spi.transformer.TransformerContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PermissionFromIdsPermissionTransformer implements IdsTypeTransformer<de.fraunhofer.iais.eis.Permission, Permission> {

    @Override
    public Class<de.fraunhofer.iais.eis.Permission> getInputType() {
        return de.fraunhofer.iais.eis.Permission.class;
    }

    @Override
    public Class<Permission> getOutputType() {
        return Permission.class;
    }

    @Override
    public @Nullable Permission transform(de.fraunhofer.iais.eis.Permission object, @NotNull TransformerContext context) {
        Objects.requireNonNull(context);
        if (object == null) {
            return null;
        }

        var builder = Permission.Builder.newInstance();
        if (object.getPostDuty() != null && !object.getPostDuty().isEmpty()) {
            context.reportProblem("Cannot map IDS permission post duty to EDC (ODRL)");
            return null;
        }

        for (var idsPreDuty : object.getPreDuty()) {
            var duty = context.transform(idsPreDuty, Duty.class);
            if (duty != null) {
                builder.duty(duty);
            }
        }

        for (var idsConstraint : object.getConstraint()) {
            var constraint = context.transform(idsConstraint, Constraint.class);
            if (constraint != null) {
                builder.constraint(constraint);
            }
        }

        if (object.getTarget() != null) {
            var target = IdsId.from(object.getTarget());
            if (target.succeeded() && target.getContent().getValue() != null) {
                builder.target(target.getContent().getValue());
            }
        }

        if (object.getAssigner() != null && object.getAssigner().size() != 0) {
            if (object.getAssigner().size() > 1) {
                context.reportProblem("Cannot map multiple IDS permission assigners to EDC (ODRL)");
            }

            builder.assigner(object.getAssigner().get(0).toString());
        }

        if (object.getAssignee() != null && object.getAssignee().size() != 0) {
            if (object.getAssignee().size() > 1) {
                context.reportProblem("Cannot map multiple IDS permission assignees to EDC (ODRL)");
            }

            builder.assignee(object.getAssignee().get(0).toString());
        }

        if (object.getAction() != null && object.getAction().size() != 0) {
            if (object.getAction().size() > 1) {
                context.reportProblem("Cannot map multiple IDS permission actions to EDC (ODRL)");
                return null;
            }

            builder.action(Action.Builder.newInstance().type(object.getAction().get(0).name()).build());
        }

        return builder.build();
    }
}
