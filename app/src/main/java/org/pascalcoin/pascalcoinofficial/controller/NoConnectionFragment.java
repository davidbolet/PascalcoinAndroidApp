package org.pascalcoin.pascalcoinofficial.controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.pascalcoin.pascalcoinofficial.R;
import org.pascalcoin.pascalcoinofficial.services.rest.PascalcoinServiceProvider;

public class NoConnectionFragment extends DialogFragment {
    private static final String TAG = NoConnectionFragment.class.getSimpleName();

    private ImageView buttonRetry;
    private String selectedPublicKey;
    private PascalcoinServiceProvider pascalcoinServiceProvider;
    private Integer initialAccount;
    private Integer numAccounts;

    public NoConnectionFragment() {}

    public static NoConnectionFragment newInstance(PascalcoinServiceProvider pascalcoinServiceProvider, String publicKey, Integer initialAccount, Integer numAccounts) {
        NoConnectionFragment fragment = new NoConnectionFragment();
        fragment.selectedPublicKey=publicKey;
        fragment.initialAccount=initialAccount;
        fragment.pascalcoinServiceProvider=pascalcoinServiceProvider;
        fragment.numAccounts=numAccounts;
        return fragment;
    }

    public void setSelectedPublicKey(String publicKey) {
        this.selectedPublicKey=publicKey;
    }

    public void setPascalcoinServiceProvider(PascalcoinServiceProvider provider) {
        this.pascalcoinServiceProvider=provider;
    }

    public void setInitialAccount(Integer num) {
        this.initialAccount=num;
    }

    public void setNumAccounts(Integer numAccounts) {
        this.numAccounts=numAccounts;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_connection_error, container, false);

        buttonRetry = result.findViewById(R.id.img_retry);
        buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pascalcoinServiceProvider.getPublicKeyAccounts(selectedPublicKey,initialAccount, numAccounts);
            }
        });
        return result;
    }

}
