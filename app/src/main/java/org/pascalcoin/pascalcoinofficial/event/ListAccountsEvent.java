package org.pascalcoin.pascalcoinofficial.event;

import com.github.davidbolet.jpascalcoin.api.model.Account;

import java.util.List;

public class ListAccountsEvent implements Event {

    private final List<Account> listAccounts;
    private final String publicKey;
    private String name;
    private Integer type;
    private Boolean sale;
    private Integer start;
    private Integer numElems;

    public ListAccountsEvent(List<Account> listAccounts, String publicKey) {
        this.listAccounts=listAccounts;
        this.publicKey=publicKey;
        this.name=null;
        this.type=null;
        this.sale=null;
        this.start=null;
        this.numElems =null;
    }

    public ListAccountsEvent(List<Account> listAccounts) {
        this.listAccounts=listAccounts;
        this.publicKey=null;
        this.name=null;
        this.type=null;
        this.sale=null;
        this.start=null;
        this.numElems =null;
    }

    public ListAccountsEvent(List<Account> listAccounts, String name, Integer type, Boolean sale, Integer start, Integer numElems, String publicKey) {
        this.listAccounts=listAccounts;
        this.publicKey=publicKey;
        this.name=name;
        this.type=type;
        this.sale=sale;
        this.start=start;
        this.numElems =numElems;
    }

    public Boolean getSale() {
        return sale;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public List<Account> getListAccounts() {
        return listAccounts;
    }

    public Integer getNumElems() {
        return numElems;
    }

    public Integer getStart() {
        return start;
    }
}
