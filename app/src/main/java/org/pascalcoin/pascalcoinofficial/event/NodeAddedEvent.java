package org.pascalcoin.pascalcoinofficial.event;

import org.pascalcoin.pascalcoinofficial.model.NodeInfo;

public class NodeAddedEvent implements Event {
    private final NodeInfo newNode;

    public NodeAddedEvent(NodeInfo node) {
        this.newNode=node;
    }

    public NodeInfo getNewNode() {
        return newNode;
    }
}


