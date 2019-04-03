package org.pascalcoin.pascalcoinofficial.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.pascalcoin.pascalcoinofficial.AccountActivity;
import org.pascalcoin.pascalcoinofficial.R;
import org.pascalcoin.pascalcoinofficial.data.Constants;
import org.pascalcoin.pascalcoinofficial.helper.PascalUtils;

import com.github.davidbolet.jpascalcoin.api.model.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CancelSellAccountFragment extends OperationFragment {

    private OnFragmentCancelSellAccountListener mListener;

    private Account accountToSell;
    private Account accountPayer;
    private TextView titleTxt;
    private Button buttonConfirm;
    private Button buttonCancel;
    private Spinner spinAccountPayer;

    //Payload fragment
    private Switch payloadSwitch;
    private EditText transferPayload;
    private Spinner encriptionMethod;
    private EditText encryptionPassword;
    public CancelSellAccountFragment() {

    }

    public static CancelSellAccountFragment newInstance(Account accountToChange, List<Account> userAccounts) {
        CancelSellAccountFragment result = new CancelSellAccountFragment();
        result.setAccountToSell(accountToChange);
        result.setUserAccounts(userAccounts);
        return result;
    }

    public void setAccountToSell(Account accountToSell) {
        this.accountToSell = accountToSell;
        this.accountPayer= accountToSell;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("accountToSell", accountToSell);
        super.onSaveInstanceState(outState);
    }

    @SuppressLint({"StringFormatMatches", "StringFormatInvalid"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_cancel_sell_account, container, false);

        titleTxt = result.findViewById(R.id.send_pasc_title);
        spinAccountPayer = result.findViewById(R.id.spinAccountPayer);
        List<String> userAccountsAsString=new ArrayList<>();
        for(Account account:userAccounts) {
            userAccountsAsString.add(String.format(Locale.getDefault(),"%d-%d (%.4f PASC)",account.getAccount(), PascalUtils.calculateChecksum(account.getAccount()),account.getBalance()));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, userAccountsAsString);
        spinAccountPayer.setAdapter(adapter);
        spinAccountPayer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                accountPayer=userAccounts.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        transferFee = result.findViewById(R.id.transfer_fee);
        buttonConfirm = result.findViewById(R.id.buttonConfirm);
        buttonCancel = result.findViewById(R.id.buttonCancel);

        //Payload
        payloadSwitch = result.findViewById(R.id.switchPayload);
        transferPayload = result.findViewById(R.id.transfer_payload);
        encriptionMethod = result.findViewById(R.id.spinnerEncriptionMethod);
        encryptionPassword = result.findViewById(R.id.encryption_password);
        setPayloadInteraction();
        if (savedInstanceState!=null && savedInstanceState.containsKey("accountToSell")) {
            accountToSell = (Account) savedInstanceState.getSerializable("accountToSell");
            accountPayer = accountToSell;
        }
        if (titleTxt!=null) {
            titleTxt.setText(getString(R.string.delist_account_sale,accountToSell.getAccount()));
        }
        transferFee.setText(Constants.defaultFeeText);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (transferFee.getText().toString().isEmpty()) {
                    transferFee.setText("0.0");
                }
                if (!balanceOk()) {
                    transferFee.setError(getText(R.string.error_not_enogh_pasc_to_transfer));
                    return;
                }
                else {
                    Bundle results = new Bundle();
                    results.putInt("accountPayer", accountPayer.getAccount());
                    results.putDouble("fee",getFee());
                    results.putInt(AccountActivity.PARAM_SELLNG_ACCOUNT, accountToSell.getAccount());
                    if (transferPayload.getVisibility()== View.VISIBLE) {
                        results.putString("payload",transferPayload.getText().toString());
                        results.putString("payloadEncription", getResources().getStringArray(R.array.payloadEncryptionMethodValue)[encriptionMethod.getSelectedItemPosition()]);
                        results.putString("payloadPassword", encryptionPassword.getText().toString());
                    }
                    onButtonPressed(results);
                    getDialog().dismiss();
                }
            }
        });

        return result;
    }

   private boolean balanceOk() {
        Double fee = Constants.defaultFeeDouble;
        if (!transferFee.getText().toString().isEmpty()) {
            fee= getFee();
        }
        return accountPayer.getBalance()>=fee;
    }

    private void setPayloadInteraction() {
        payloadSwitch.setChecked(false);
        payloadSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchPayloadVisibility(isChecked);
            }
        });
        encriptionMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 3) {
                    encryptionPassword.setVisibility(View.VISIBLE);
                }
                else
                    encryptionPassword.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                encryptionPassword.setVisibility(View.GONE);
            }

        });
    }

    private void switchPayloadVisibility(boolean visible) {
        if (visible) {
            transferPayload.setVisibility(View.VISIBLE);
            encriptionMethod.setVisibility(View.VISIBLE);
            encryptionPassword.setVisibility( encriptionMethod.getSelectedItemPosition()==3? View.VISIBLE: View.GONE);
        } else {
            transferPayload.setVisibility(View.GONE);
            encriptionMethod.setVisibility(View.GONE);
            encryptionPassword.setVisibility(View.GONE);
        }

    }

    public void onButtonPressed(Bundle bundle) {
        if (mListener != null) {
            mListener.onCancelSellAccount(bundle, accountPayer, accountToSell);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentCancelSellAccountListener) {
            mListener = (OnFragmentCancelSellAccountListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentChangeAccountListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    public interface OnFragmentCancelSellAccountListener {
        void onCancelSellAccount(Bundle params, final Account accountFrom,final Account accountOnSale);
    }

}
