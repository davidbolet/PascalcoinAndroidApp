package org.pascalcoin.pascalcoinofficial.event;


import com.github.davidbolet.jpascalcoin.api.model.Account;

public class AccountInfoEvent implements Event {
    final Account account;

    public AccountInfoEvent(Account account) {
        this.account=account;
    }

    public Account getAccount() {
        return this.account;
    }
}
