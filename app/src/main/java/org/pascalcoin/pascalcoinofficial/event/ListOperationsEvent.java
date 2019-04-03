package org.pascalcoin.pascalcoinofficial.event;

import com.github.davidbolet.jpascalcoin.api.model.Operation;

import java.util.List;

public class ListOperationsEvent implements Event {

    private final List<Operation> listOperations;
    private final Integer account;
    private final Integer start;
    private final Integer numElems;

    public ListOperationsEvent(List<Operation> operations, Integer account, Integer start, Integer numElems) {
        this.listOperations =operations;
        this.account=account;
        this.start=start;
        this.numElems=numElems;
    }

    public List<Operation> getListOperations() {
        return listOperations;
    }

    public Integer getAccount() {
        return account;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getNumElems() {
        return numElems;
    }
}
