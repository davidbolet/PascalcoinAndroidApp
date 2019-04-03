package org.pascalcoin.pascalcoinofficial.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import android.widget.Toast;

import com.github.davidbolet.jpascalcoin.api.model.Account;
import com.github.davidbolet.jpascalcoin.common.model.PascPublicKey;

import org.pascalcoin.pascalcoinofficial.AccountActivity;
import org.pascalcoin.pascalcoinofficial.R;
import org.pascalcoin.pascalcoinofficial.data.Constants;
import org.pascalcoin.pascalcoinofficial.helper.PascalUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BuyAccountFragment extends OperationFragment {

    private static BuyAccountFragment _instance;

    private OnFragmentBuyAccountListener mListener;
    private Account accountToBuy;
    private Account accountPayer;
    private Account accountSeller;
    private EditText editTextPrice;
    private TextView accountSellerTxt;
    private TextView titleTxt;
    private Button buttonConfirm;
    private Button buttonCancel;
    private Spinner spinAccountPayer;
    //Payload fragment
    private Switch payloadSwitch;
    private EditText transferPayload;
    private Spinner encriptionMethod;
    private EditText encryptionPassword;
    public BuyAccountFragment() {

    }

    public static BuyAccountFragment newInstance(Account accountToBuy, List<Account> userAccounts) {
        if (_instance==null)
            _instance = new BuyAccountFragment();
        _instance.setAccountToBuy(accountToBuy);
        _instance.setUserAccounts(userAccounts);
        return _instance;
    }

    public void setAccountToBuy(Account accountToBuy) {
        this.accountToBuy = accountToBuy;
    }

    public void setAccountSeller(Account accountSeller) {
        this.accountSeller = accountSeller;
    }

    public void setAccountPayer(Account accountPayer) {
        this.accountPayer = accountPayer;
        if (spinAccountPayer!=null && userAccounts!=null && accountPayer!=null)
            spinAccountPayer.setSelection(userAccounts.indexOf(accountPayer));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("accountToSell", accountToBuy);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (spinAccountPayer!=null && userAccounts!=null && accountPayer!=null)
            spinAccountPayer.setSelection(userAccounts.indexOf(accountPayer));
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_buy_account, container, false);

        editTextPrice = result.findViewById(R.id.sell_price);
        titleTxt = result.findViewById(R.id.send_pasc_title);
        spinAccountPayer = result.findViewById(R.id.spinAccountPayer);
        List<String> userAccountsAsString=new ArrayList<>();
        for(Account account:userAccounts) {
            userAccountsAsString.add(String.format(Locale.getDefault(),"%d-%d (%.4f PASC)",account.getAccount(),PascalUtils.calculateChecksum(account.getAccount()),account.getBalance()));
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
        accountSellerTxt = result.findViewById(R.id.txtAccountSeller);
        transferFee = result.findViewById(R.id.transfer_fee);
        buttonConfirm = result.findViewById(R.id.buttonConfirm);
        buttonCancel = result.findViewById(R.id.buttonCancel);
        //Payload
        payloadSwitch = result.findViewById(R.id.switchPayload);
        transferPayload = result.findViewById(R.id.transfer_payload);
        encriptionMethod = result.findViewById(R.id.spinnerEncriptionMethod);
        encryptionPassword = result.findViewById(R.id.encryption_password);
        setPayloadInteraction();
        if (savedInstanceState!=null && savedInstanceState.containsKey("accountToBuy")) {
            accountToBuy= (Account) savedInstanceState.getSerializable("accountToBuy");
        }
        if (titleTxt!=null) {
            titleTxt.setText(getString(R.string.buy_account_sale,accountToBuy.getAccount()));
        }
        accountSellerTxt.setText(""+accountToBuy.getSellerAccount()+"-"+ PascalUtils.calculateChecksum(accountToBuy.getSellerAccount()));
        editTextPrice.setText(String.format( "%.4f", accountToBuy.getPrice()));
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
                if (editTextPrice.getText().toString().isEmpty()) {
                    editTextPrice.setError(getText(R.string.error_sell_price_account_requiered));
                    return;
                }
                if (!balanceOk()) {
                    transferFee.setError(getText(R.string.error_not_enogh_pasc_to_transfer));
                    return;
                }
                else {
                    Bundle results = new Bundle();
                    results.putInt("accountToBuy", accountToBuy.getAccount());
                    results.putInt("accountPayer", accountPayer.getAccount());
                    results.putDouble("price",getPrice());
                    results.putDouble("fee",getFee());
                    results.putInt(AccountActivity.PARAM_BUYING_ACCOUNT, accountToBuy.getAccount());
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


    private Double getPrice() {
        if (editTextPrice.getText().toString().isEmpty()) return 0.0;
        Double result=0.0;
        try {
            result = Double.parseDouble(editTextPrice.getText().toString().replace(',', '.'));;
        } catch(NumberFormatException pe) {
            Log.e("BUYACCOUNT",pe.getMessage());
        }
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
            if (accountSeller==null)
            {
                mListener.retryDetermineAccountSeller(accountToBuy.getSellerAccount());
                Toast.makeText((Context)mListener,"Could not determine accountSeller key. Retriying",Toast.LENGTH_LONG);

            } else
                mListener.onBuyAccount(bundle,accountToBuy,accountPayer,PascPublicKey.fromEncodedPubKey(accountSeller.getEncPubkey()));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentBuyAccountListener) {
            mListener = (OnFragmentBuyAccountListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentBuyAccountListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    public interface OnFragmentBuyAccountListener {
        void onBuyAccount(Bundle params, final Account accountToBuy, final Account accountPayer, final PascPublicKey sellerPublicKey);
        void retryDetermineAccountSeller(Integer accountSeller);
    }

}
