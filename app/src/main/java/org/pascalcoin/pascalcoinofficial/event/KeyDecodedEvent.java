package org.pascalcoin.pascalcoinofficial.event;

import org.pascalcoin.pascalcoinofficial.model.PrivateKeyInfo;

public class KeyDecodedEvent {

    private final PrivateKeyInfo keyInfo;
    private final String password;

    public KeyDecodedEvent(PrivateKeyInfo keyInfo, String password) {
        this.keyInfo=keyInfo;
        this.password=password;
    }

    public PrivateKeyInfo getKeyInfo() {
        return keyInfo;
    }

    public String getPassword() {
        return password;
    }
}
