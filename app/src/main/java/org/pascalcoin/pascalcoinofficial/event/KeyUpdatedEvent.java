package org.pascalcoin.pascalcoinofficial.event;

import org.pascalcoin.pascalcoinofficial.model.PrivateKeyInfo;

public class KeyUpdatedEvent implements Event {

    private final PrivateKeyInfo keyInfo;

    public KeyUpdatedEvent(PrivateKeyInfo keyInfo) {
        this.keyInfo=keyInfo;
    }

    public PrivateKeyInfo getKeyInfo() {
        return keyInfo;
    }
}
