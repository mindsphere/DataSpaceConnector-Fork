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

package org.eclipse.dataspaceconnector.api.datamanagement.transferprocess.service;

import net.datafaker.Faker;
import org.eclipse.dataspaceconnector.spi.query.QuerySpec;
import org.eclipse.dataspaceconnector.spi.response.StatusResult;
import org.eclipse.dataspaceconnector.spi.transaction.NoopTransactionContext;
import org.eclipse.dataspaceconnector.spi.transaction.TransactionContext;
import org.eclipse.dataspaceconnector.spi.transfer.TransferProcessManager;
import org.eclipse.dataspaceconnector.spi.transfer.store.TransferProcessStore;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataRequest;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.TransferProcess;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.TransferProcessStates;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.command.CancelTransferCommand;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.command.DeprovisionRequest;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.command.SingleTransferProcessCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class TransferProcessServiceImplTest {
    static Faker faker = new Faker();

    String id = faker.lorem().word();
    TransferProcess process1 = transferProcess();
    TransferProcess process2 = transferProcess();
    QuerySpec query = QuerySpec.Builder.newInstance().limit(5).offset(2).build();
    ArgumentCaptor<SingleTransferProcessCommand> commandCaptor = ArgumentCaptor.forClass(SingleTransferProcessCommand.class);

    TransferProcessStore store = mock(TransferProcessStore.class);
    TransferProcessManager manager = mock(TransferProcessManager.class);
    TransactionContext transactionContext = spy(new NoopTransactionContext());

    TransferProcessService service = new TransferProcessServiceImpl(store, manager, transactionContext);

    @AfterEach
    void after() {


    }

    @Test
    void findById_whenFound() {
        when(store.find(id)).thenReturn(process1);
        assertThat(service.findById(id)).isSameAs(process1);
        verify(transactionContext).execute(any(TransactionContext.ResultTransactionBlock.class));
    }

    @Test
    void findById_whenNotFound() {
        assertThat(service.findById(id)).isNull();
        verify(transactionContext).execute(any(TransactionContext.ResultTransactionBlock.class));
    }

    @Test
    void query() {
        when(store.findAll(query)).thenReturn(Stream.of(process1, process2));
        assertThat(service.query(query).getContent()).containsExactly(process1, process2);
        verify(transactionContext).execute(any(TransactionContext.ResultTransactionBlock.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "provisionedResourceSet.resources.hastoken=true", //wrong case
            "resourceManifest.definitions.notexist=foobar", //property not exist
            "contentDataAddress.properties.someKey=someval", //map types not supported
    })
    void query_invalidFilter_raiseException(String invalidFilter) {
        var spec = QuerySpec.Builder.newInstance().filter(invalidFilter).build();
        assertThat(service.query(spec).failed()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "deprovisionedResources.provisionedResourceId=19",
            "type=CONSUMER",
            "provisionedResourceSet.resources.hasToken=true"
    })
    void query_validFilter(String validFilter) {
        var spec = QuerySpec.Builder.newInstance().filter(validFilter).build();
        service.query(spec);
        verify(store).findAll(spec);
        verify(transactionContext).execute(any(TransactionContext.ResultTransactionBlock.class));
    }

    @Test
    void getState_whenFound() {
        when(store.find(id)).thenReturn(process1);
        assertThat(service.getState(id)).isEqualTo(TransferProcessStates.from(process1.getState()).name());
        verify(transactionContext).execute(any(TransactionContext.ResultTransactionBlock.class));
    }

    @Test
    void getState_whenNotFound() {
        assertThat(service.getState(id)).isNull();
        verify(transactionContext).execute(any(TransactionContext.ResultTransactionBlock.class));
    }

    @ParameterizedTest
    @EnumSource(value = TransferProcessStates.class, mode = EXCLUDE, names = { "COMPLETED", "ERROR", "ENDED" })
    void cancel(TransferProcessStates state) {
        var process = transferProcess(state, id);
        when(store.find(id)).thenReturn(process);

        var result = service.cancel(id);

        assertThat(result.succeeded()).isTrue();
        verify(manager).enqueueCommand(commandCaptor.capture());
        assertThat(commandCaptor.getValue()).isInstanceOf(CancelTransferCommand.class);
        assertThat(commandCaptor.getValue().getTransferProcessId())
                .isEqualTo(id);
        verify(transactionContext).execute(any(TransactionContext.ResultTransactionBlock.class));
    }

    @ParameterizedTest
    @EnumSource(value = TransferProcessStates.class, mode = INCLUDE, names = { "COMPLETED", "ERROR", "ENDED" })
    void cancel_whenNonCancellable(TransferProcessStates state) {
        var process = transferProcess(state, id);
        when(store.find(id)).thenReturn(process);

        var result = service.cancel(id);

        assertThat(result.failed()).isTrue();
        assertThat(result.getFailureMessages()).containsExactly("TransferProcess " + process.getId() + " cannot be canceled as it is in state " + state);
        verifyNoInteractions(manager);
        verify(transactionContext).execute(any(TransactionContext.ResultTransactionBlock.class));
    }

    @Test
    void cancel_whenNotFound() {
        var result = service.cancel(id);

        assertThat(result.failed()).isTrue();
        assertThat(result.getFailureMessages()).containsExactly("TransferProcess " + id + " does not exist");
        verify(transactionContext).execute(any(TransactionContext.ResultTransactionBlock.class));
    }

    @Test
    void initiateTransfer() {
        var dataRequest = DataRequest.Builder.newInstance().destinationType("type").build();
        String processId = "processId";
        when(manager.initiateConsumerRequest(dataRequest)).thenReturn(StatusResult.success(processId));

        var result = service.initiateTransfer(dataRequest);

        assertThat(result.succeeded()).isTrue();
        assertThat(result.getContent()).isEqualTo(processId);
        verify(transactionContext).execute(any(TransactionContext.ResultTransactionBlock.class));
    }

    @ParameterizedTest
    @EnumSource(value = TransferProcessStates.class, mode = INCLUDE, names = { "COMPLETED", "DEPROVISIONING", "DEPROVISIONED", "ENDED", "CANCELLED" })
    void deprovision(TransferProcessStates state) {
        var process = transferProcess(state, id);
        when(store.find(id)).thenReturn(process);

        var result = service.deprovision(id);

        assertThat(result.succeeded()).isTrue();
        verify(manager).enqueueCommand(commandCaptor.capture());
        assertThat(commandCaptor.getValue()).isInstanceOf(DeprovisionRequest.class);
        assertThat(commandCaptor.getValue().getTransferProcessId())
                .isEqualTo(id);
        verify(transactionContext).execute(any(TransactionContext.ResultTransactionBlock.class));
    }

    @ParameterizedTest
    @EnumSource(value = TransferProcessStates.class, mode = EXCLUDE, names = { "COMPLETED", "DEPROVISIONING", "DEPROVISIONED", "ENDED", "CANCELLED" })
    void deprovision_whenNonDeprovisionable(TransferProcessStates state) {
        var process = transferProcess(state, id);
        when(store.find(id)).thenReturn(process);

        var result = service.deprovision(id);

        assertThat(result.failed()).isTrue();
        assertThat(result.getFailureMessages()).containsExactly("TransferProcess " + process.getId() + " cannot be deprovisioned as it is in state " + state);
        verifyNoInteractions(manager);
        verify(transactionContext).execute(any(TransactionContext.ResultTransactionBlock.class));
    }

    @Test
    void deprovision_whenNotFound() {
        var result = service.deprovision(id);

        assertThat(result.failed()).isTrue();
        assertThat(result.getFailureMessages()).containsExactly("TransferProcess " + id + " does not exist");
        verify(transactionContext).execute(any(TransactionContext.ResultTransactionBlock.class));
    }

    private TransferProcess transferProcess() {
        return transferProcess(faker.options().option(TransferProcessStates.class), UUID.randomUUID().toString());
    }

    private TransferProcess transferProcess(TransferProcessStates state, String id) {
        return TransferProcess.Builder.newInstance()
                .state(state.code())
                .id(id)
                .build();
    }
}