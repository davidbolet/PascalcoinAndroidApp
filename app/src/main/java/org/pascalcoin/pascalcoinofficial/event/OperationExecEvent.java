package org.pascalcoin.pascalcoinofficial.event;

import com.github.davidbolet.jpascalcoin.api.model.Operation;
import com.github.davidbolet.jpascalcoin.crypto.model.PascOperation;

import java.util.List;

public class OperationExecEvent implements Event {

    private final List<Operation> listOperations;
    private final PascOperation operation;


    public OperationExecEvent(List<Operation> operations, PascOperation operation) {
        this.listOperations =operations;
        this.operation=operation;

    }
    public List<Operation> getListOperations() {
        return listOperations;
    }

    public PascOperation getOperation() {
        return operation;
    }
}
