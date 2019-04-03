package org.pascalcoin.pascalcoinofficial.event;

/**
 * Created by davidbolet on 3/11/17.
 */

public class ErrorEvent implements Event {

    private String errorMsg;
    private int errorCode=0;

    public ErrorEvent(String errorMsg) {
        super();
        this.errorMsg =errorMsg;
    }

    public ErrorEvent(String errorMsg, int errorCode) {
        super();
        this.errorMsg =errorMsg;
        this.errorCode=errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}
