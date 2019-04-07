package org.pascalcoin.pascalcoinofficial.event;

import org.pascalcoin.pascalcoinofficial.model.NodeInfo;

public class NodeUpdatedEvent implements Event {
    /**
     *
     */
    private final NodeInfo updatedNode;

    public NodeUpdatedEvent(NodeInfo node) {
        this.updatedNode =node;
    }

    public NodeInfo getUpdatedNode() {
        return updatedNode;
    }
}


