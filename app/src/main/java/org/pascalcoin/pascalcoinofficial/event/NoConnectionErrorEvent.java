package org.pascalcoin.pascalcoinofficial.event;

public class NoConnectionErrorEvent implements Event {
    private final String method;

    public NoConnectionErrorEvent(String methodCalled) {
        this.method=methodCalled;
    }

    public String getMethod() {
        return method;
    }
}
