package org.pascalcoin.pascalcoinofficial.event;

public class NodeDeletedEvent implements Event {

    private final String deletedNodeName;
    public NodeDeletedEvent(String toDelete) {
        this.deletedNodeName=toDelete;
    }

    public String getDeletedNodeName() {
        return deletedNodeName;
    }
}
