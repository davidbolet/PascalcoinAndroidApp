package org.pascalcoin.pascalcoinofficial.event;

import org.pascalcoin.pascalcoinofficial.model.PrivateKeyInfo;

public class KeyAddedEvent implements Event {

    private final PrivateKeyInfo keyInfo;

    public KeyAddedEvent(PrivateKeyInfo keyInfo) {
        this.keyInfo=keyInfo;
    }

    public PrivateKeyInfo getKeyInfo() {
        return keyInfo;
    }
}
