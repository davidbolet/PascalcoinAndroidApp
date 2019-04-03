package org.pascalcoin.pascalcoinofficial.event;

public class KeyDeletedEvent implements Event {
    private final String deletedKeyName;

    public KeyDeletedEvent(String name) {
        deletedKeyName=name;
    }

    public String getDeletedKeyName() {
        return deletedKeyName;
    }
}
