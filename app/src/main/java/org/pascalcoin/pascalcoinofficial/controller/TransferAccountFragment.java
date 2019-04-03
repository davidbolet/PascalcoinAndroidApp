package org.pascalcoin.pascalcoinofficial.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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

import com.github.davidbolet.jpascalcoin.api.model.Account;

import org.pascalcoin.pascalcoinofficial.AccountActivity;
import org.pascalcoin.pascalcoinofficial.R;
import org.pascalcoin.pascalcoinofficial.data.Constants;
import org.pascalcoin.pascalcoinofficial.helper.PascalUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransferAccountFragment extends OperationFragment {

    private OnFragmentSendAccountListener mListener;
    private Account accountToTransfer;
    private Account accountPayer;
    private TextView sendAccountIntro;
    private EditText destinationAddress;
    private ImageButton scanQRButton;
    private Button buttonConfirm;
    private Button buttonCancel;
    private Spinner spinAccountPayer;

    //Payload fragment
    private Switch payloadSwitch;
    private EditText transferPayload;
    private Spinner encriptionMethod;
    private EditText encryptionPassword;

    public TransferAccountFragment() {

    }

    public static TransferAccountFragment newInstance(Account accountToTransfer, List<Account> userAccounts) {
        TransferAccountFragment result = new TransferAccountFragment();
        result.setAccountToTransfer(accountToTransfer);
        result.setUserAccounts(userAccounts);
        return result;
    }

    public void setAccountToTransfer(Account accountToTransfer) {
        this.accountToTransfer=accountToTransfer;
        this.accountPayer=accountToTransfer;
    }

    public void setUserAccounts(List<Account> userAccounts) {
        this.userAccounts=userAccounts;
    }

    public void setDestinationAddress(String destinationAddress) {
        if (this.destinationAddress!=null) {
            this.destinationAddress.setText(destinationAddress);
            this.destinationAddress.refreshDrawableState();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("accountToTransfer", accountToTransfer);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (spinAccountPayer!=null && userAccounts!=null && accountToTransfer!=null)
            spinAccountPayer.setSelection(userAccounts.indexOf(accountToTransfer));
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_send_account, container, false);
        sendAccountIntro = result.findViewById(R.id.send_account_intro);
        destinationAddress = result.findViewById(R.id.destination_pk);
        scanQRButton = result.findViewById(R.id.buttonScanAddress);
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
        transferFee = result.findViewById(R.id.transfer_fee);
        buttonConfirm = result.findViewById(R.id.buttonConfirm);
        buttonCancel = result.findViewById(R.id.buttonCancel);
        //Payload
        payloadSwitch = result.findViewById(R.id.switchPayload);
        transferPayload = result.findViewById(R.id.transfer_payload);
        encriptionMethod = result.findViewById(R.id.spinnerEncriptionMethod);
        encryptionPassword = result.findViewById(R.id.encryption_password);
        setPayloadInteraction();
        if (savedInstanceState!=null && savedInstanceState.containsKey("accountToTransfer")) {
            accountToTransfer = (Account) savedInstanceState.getSerializable("accountToTransfer");
            accountPayer = accountToTransfer;
        }
        sendAccountIntro.setText(getString(R.string.txt_send_account_intro,accountToTransfer.getAccount(), PascalUtils.calculateChecksum(accountToTransfer.getAccount()),accountToTransfer.getBalance()));
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
                if (destinationAddress.getText().toString().isEmpty()) {
                    destinationAddress.setError(getText(R.string.error_destination_account_requiered));
                    return;
                }
                if (!balanceOk()) {
                    transferFee.setError(getText(R.string.error_not_enogh_pasc_to_transfer));
                    return;
                }
                else {
                    Bundle results = new Bundle();
                    results.putString("newPubKey",destinationAddress.getText().toString());
                    results.putInt("accountPayer", accountPayer.getAccount());
                    results.putDouble("fee",getFee());
                    results.putInt("accountToTransfer",accountToTransfer.getAccount());
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
        scanQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
                    getActivity().startActivityForResult(intent, AccountActivity.SCAN_CODE);

                } catch (Exception e) {

                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
                    startActivity(marketIntent);
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

    public void onButtonPressed(Bundle uri) {
        if (mListener != null) {
            mListener.onSendAccount(uri, accountPayer, accountToTransfer);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentSendAccountListener) {
            mListener = (OnFragmentSendAccountListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    public interface OnFragmentSendAccountListener {
        void onSendAccount(Bundle params, Account accountPayer, Account accountToTransfer);
    }

}
