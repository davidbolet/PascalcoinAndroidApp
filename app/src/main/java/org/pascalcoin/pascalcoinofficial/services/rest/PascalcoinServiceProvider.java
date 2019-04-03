package org.pascalcoin.pascalcoinofficial.services.rest;

import android.util.Log;

import com.github.davidbolet.jpascalcoin.api.client.PascalCoinClient;
import com.github.davidbolet.jpascalcoin.api.client.PascalCoinClientImpl;
import com.github.davidbolet.jpascalcoin.api.model.Account;
import com.github.davidbolet.jpascalcoin.api.model.OpResult;
import com.github.davidbolet.jpascalcoin.api.model.Operation;
import com.github.davidbolet.jpascalcoin.crypto.model.PascOperation;

import org.greenrobot.eventbus.EventBus;
import org.pascalcoin.pascalcoinofficial.event.AccountInfoEvent;
import org.pascalcoin.pascalcoinofficial.event.ListAccountsEvent;
import org.pascalcoin.pascalcoinofficial.event.ListOperationsEvent;
import org.pascalcoin.pascalcoinofficial.event.OperationExecEvent;


import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PascalcoinServiceProvider {
    private static final String TAG = PascalcoinServiceProvider.class.getSimpleName();
    private final static Integer defaultLogLevel=0;

    private static PascalcoinServiceProvider _instance;

    private PascalCoinClient pascalCoinClient;
    private String baseUrl;

    private PascalcoinServiceProvider(String baseUrl) {
        try {
            this.baseUrl=baseUrl;
            pascalCoinClient = new PascalCoinClientImpl(baseUrl, defaultLogLevel);
        } catch(Exception ex) {
            Log.e("main","Error connecting to node",ex);
        }
    }

    public static PascalcoinServiceProvider getInstance(String baseUrl) {
        if (_instance==null || !baseUrl.equals(_instance.baseUrl)) {
            if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) baseUrl="http://"+baseUrl;
                _instance = new PascalcoinServiceProvider(baseUrl);
        }
        return _instance;
    }

    public void getPublicKeyAccounts(final String encPubKey, final Integer start, final Integer max)
    {
        pascalCoinClient.findAccountsAsync(null, null, null, null, encPubKey, null, null, null,  start,  max, new Callback<OpResult<List<Account>>>() {
            @Override
            public void onResponse(Call<OpResult<List<Account>>> call, Response<OpResult<List<Account>>> response) {
                ListAccountsEvent event;
                OpResult<List<Account>> result= response.body();
                if (result==null) {
                    Log.e(TAG,String.format("Error retrieving account's list. StatusCode %d, message %s",response.code(), response.message()));
                    return;
                }
                if (result.isError()) {
                    //If there's no timeout, the error is because public key is not on the safebox, as maybe new or without any accounts
                    Log.e(TAG,"Error retrieving account's list. Error is "+result.getErrorMessage());
                    event = new ListAccountsEvent(new ArrayList<Account>(), encPubKey);
                } else {
                    event = new ListAccountsEvent(result.getResult(), encPubKey);
                }
                EventBus.getDefault().post(event);
            }

            @Override
            public void onFailure(Call<OpResult<List<Account>>> call, Throwable t) {
                Log.e(TAG,"Error occurred:"+t.getMessage() );
                if (t instanceof SocketTimeoutException) {
                    EventBus.getDefault().post(new org.pascalcoin.pascalcoinofficial.event.NoConnectionErrorEvent("getPublicKeyAccounts"));
                }
                else
                    EventBus.getDefault().post(new org.pascalcoin.pascalcoinofficial.event.ErrorEvent("Could not get account list for public key.Exception is "+t.getMessage()));
            }
        });
    }

    public void getAccount(final Integer account) {
        pascalCoinClient.getAccountAsync(account, new Callback<OpResult<Account>>() {

            @Override
            public void onResponse(Call<OpResult<Account>> call, Response<OpResult<Account>> response) {
                OpResult<Account> result=response.body();
                if (result==null) {
                    Log.e(TAG,String.format("Error retrieving account info. StatusCode %d, message %s",response.code(), response.message()));
                    return;
                }
                if (result.isError()) {
                    Log.e(TAG,"Error retrieving account info. Error is "+result.getErrorMessage());
                    EventBus.getDefault().post(new org.pascalcoin.pascalcoinofficial.event.ErrorEvent("Error retrieving account info. Error is "+result.getErrorMessage()));
                } else {
                    AccountInfoEvent accountInfoEvent=new AccountInfoEvent(result.getResult());
                    EventBus.getDefault().post(accountInfoEvent);
                }
            }

            @Override
            public void onFailure(Call<OpResult<Account>> call, Throwable t) {
                Log.e(TAG,"Error occurred:"+t.getMessage() );
                EventBus.getDefault().post(new org.pascalcoin.pascalcoinofficial.event.ErrorEvent("Could not retrieve account's info.Exception is "+t.getMessage()));
            }
        });
    }

    public void findAccounts(final String name, final Integer type, final Boolean listed, final  String encPubKey, final Double minBalance, final Double maxBalance, final Integer start, final Integer max) {
        pascalCoinClient.findAccountsAsync( name, false,  type,  listed,  null, null,  minBalance,  maxBalance,  start,  max, new Callback<OpResult<List<Account>>>() {

            @Override
            public void onResponse(Call<OpResult<List<Account>>> call, Response<OpResult<List<Account>>> response) {
                if (response.body()==null) {
                    Log.e(TAG,String.format("Error retrieving account's list. StatusCode %d, message %s",response.code(), response.message()));
                    return;
                }
                if (response.body().isError()) {
                    Log.e(TAG,"Error retrieving account's list search. Error is "+response.body().getErrorMessage());
                    EventBus.getDefault().post(new org.pascalcoin.pascalcoinofficial.event.ErrorEvent("Error retrieving account's list search. Error is "+response.body().getErrorMessage()));
                }
                else {
                    List<Account> accounts = response.body().getResult();
                    Log.e(TAG, String.format("Account search returned %d results",accounts.size()));
                    EventBus.getDefault().post(new ListAccountsEvent(accounts,name,type,listed,start,max, encPubKey));
                }
            }

            @Override
            public void onFailure(Call<OpResult<List<Account>>> call, Throwable t) {
                Log.e(TAG,"Error occurred:"+t.getMessage() );
                EventBus.getDefault().post(new org.pascalcoin.pascalcoinofficial.event.ErrorEvent("Could not retrieve account's info.Exception is "+t.getMessage()));
            }
        });
    }

    public void getAccountOperations(final Integer account, final Integer start, final Integer max)
    {
        pascalCoinClient.getAccountOperationsAsync(account, null, null, null, null,  new Callback<OpResult<List<Operation>>>() {
            @Override
            public void onResponse(Call<OpResult<List<Operation>>> call, Response<OpResult<List<Operation>>> response) {
                OpResult<List<Operation>> result= response.body();
                if (result==null) {
                    Log.e(TAG,String.format("Error retrieving operations. StatusCode %d, message %s",response.code(), response.message()));
                    return;
                }
                if (result.isError()) {
                    Log.e(TAG,"Error retrieving account operation's list. Error is "+result.getErrorMessage());
                    EventBus.getDefault().post(new org.pascalcoin.pascalcoinofficial.event.ErrorEvent("Error retrieving account operations list. Error is "+result.getErrorMessage()));

                }
                else
                {
                    ListOperationsEvent event=new ListOperationsEvent(result.getResult(),account,start,max);
                    EventBus.getDefault().post(event);
                }
            }

            @Override
            public void onFailure(Call<OpResult<List<Operation>>> call, Throwable t) {
                Log.e(TAG,"Error occurred:"+t.getMessage() );
                EventBus.getDefault().post(new org.pascalcoin.pascalcoinofficial.event.ErrorEvent("Could not get operations list for account.Exception is "+t.getMessage()));
            }
        });
    }

    public void executeOperations(final String rawOps, final PascOperation operation) {
        pascalCoinClient.executeOperationsAsync(rawOps, new Callback<OpResult<List<Operation>>>() {
            @Override
            public void onResponse(Call<OpResult<List<Operation>>> call, Response<OpResult<List<Operation>>> response) {
                OpResult<List<Operation>> result= response.body();
                if (result==null) {
                    Log.e(TAG,String.format("Error executing operation. StatusCode %d, message %s",response.code(), response.message()));
                    return;
                }
                if (result.isError()) {
                    Log.e(TAG,"Error executing operation. Error is "+result.getErrorMessage());
                    EventBus.getDefault().post(new org.pascalcoin.pascalcoinofficial.event.ErrorEvent("Error retrieving account operations list. Error is "+result.getErrorMessage()));
                }
                else
                {
                    List<Operation> res=result.getResult();
                    if (res!=null && res.size()>0) {
                        OperationExecEvent event = new OperationExecEvent(result.getResult(), operation);
                        EventBus.getDefault().post(event);
                    } else {
                        Log.e(TAG,"Error executing operation. Undefined error");
                        EventBus.getDefault().post(new org.pascalcoin.pascalcoinofficial.event.ErrorEvent("Error executing operation. Undefined error"));
                    }
                }
            }

            @Override
            public void onFailure(Call<OpResult<List<Operation>>> call, Throwable t) {
                Log.e(TAG,"Error occurred:"+t.getMessage() );
                EventBus.getDefault().post(new org.pascalcoin.pascalcoinofficial.event.ErrorEvent("Could not execute operation .Exception is "+t.getMessage()));
            }
        });

    }
}
