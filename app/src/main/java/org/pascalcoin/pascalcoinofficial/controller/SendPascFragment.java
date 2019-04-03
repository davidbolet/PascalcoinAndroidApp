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
import org.pascalcoin.pascalcoinofficial.R;
import org.pascalcoin.pascalcoinofficial.data.Constants;
import org.pascalcoin.pascalcoinofficial.helper.PascalUtils;
import org.pascalcoin.pascalcoinofficial.services.rest.PascalcoinServiceProvider;

import com.github.davidbolet.jpascalcoin.api.model.Account;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SendPascFragment extends OperationFragment  {

    private PascalcoinServiceProvider pascalcoinServiceProvider;
    private OnFragmentSendPascListener mListener;
    private Account accountFrom;
    private Account accountDest;
    private TextView sendPascIntro;
    private EditText destinationAccount;
    private EditText destinationAccountChecksum;
    private EditText transferAmount;
    private Button buttonConfirm;
    private Button buttonCancel;
    private Spinner spinAccountPayer;

    //Payload fragment
    private Switch payloadSwitch;
    private EditText transferPayload;
    private Spinner encriptionMethod;
    private EditText encryptionPassword;

    public SendPascFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SendPascFragment.
     */
    public static SendPascFragment newInstance(Account accountfrom, List<Account> userAccounts, PascalcoinServiceProvider pascalcoinServiceProvider) {
        SendPascFragment fragment = new SendPascFragment();
        fragment.setAccountFrom(accountfrom);
        fragment.userAccounts=userAccounts;
        fragment.setPascalcoinServiceProvider(pascalcoinServiceProvider);
        return fragment;
    }

    public void setAccountDest(Account account) {
        this.accountDest=account;
        if (this.destinationAccount!=null) {
            this.destinationAccount.setText(String.valueOf(account.getAccount()));
            this.destinationAccountChecksum.setText(String.valueOf(PascalUtils.calculateChecksum(account.getAccount())));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("accountFrom", accountFrom);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (spinAccountPayer!=null && userAccounts!=null && accountFrom!=null)
            spinAccountPayer.setSelection(userAccounts.indexOf(accountFrom));
        if (this.destinationAccount!=null && accountDest!=null) {
            this.destinationAccount.setText(String.valueOf(accountDest.getAccount()));
            this.destinationAccountChecksum.setText(String.valueOf(PascalUtils.calculateChecksum(accountDest.getAccount())));
        }
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_send_pasc, container, false);

        sendPascIntro = result.findViewById(R.id.send_pasc_intro);
        destinationAccount = result.findViewById(R.id.destination_account);
        destinationAccountChecksum = result.findViewById(R.id.destination_account_checksum);
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
                accountFrom=userAccounts.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        transferAmount = result.findViewById(R.id.transfer_amount);
        transferFee = result.findViewById(R.id.transfer_fee);
        buttonConfirm = result.findViewById(R.id.buttonConfirm);
        buttonCancel = result.findViewById(R.id.buttonCancel);
        //Payload
        payloadSwitch = result.findViewById(R.id.switchPayload);
        transferPayload = result.findViewById(R.id.transfer_payload);
        encriptionMethod = result.findViewById(R.id.spinnerEncriptionMethod);
        encryptionPassword = result.findViewById(R.id.encryption_password);
        setPayloadInteraction();
        if (savedInstanceState!=null && savedInstanceState.containsKey("accountFrom"))
            accountFrom=(Account) savedInstanceState.getSerializable("accountFrom");
        sendPascIntro.setText(getString(R.string.txt_send_pasc_from,accountFrom.getAccount(), PascalUtils.calculateChecksum(accountFrom.getAccount()),accountFrom.getBalance()));
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
                if (destinationAccount.getText().toString().isEmpty() || destinationAccountChecksum.getText().toString().isEmpty()) {
                    destinationAccount.setError(getText(R.string.error_destination_account_requiered));
                    return;
                }
                if (PascalUtils.calculateChecksum(Integer.parseInt(destinationAccount.getText().toString()))!=Integer.parseInt(destinationAccountChecksum.getText().toString())) {
                    destinationAccount.setError(getText(R.string.error_invalid_account_specified));
                    return;
                }
                if (destinationAccount.getText().toString().equals(accountFrom.getAccount().toString())) {
                    destinationAccount.setError(getText(R.string.error_destination_account_different));
                    return;
                }
                if (transferAmount.getText().toString().isEmpty() || getAmount()<=0) {
                    transferAmount.setError(getText(R.string.error_invalid_amount));
                    return;
                }
                if (transferFee.getText().toString().isEmpty()) {
                    transferFee.setText("0");
                }
                if (!balanceOk()) {
                    transferAmount.setError(getText(R.string.error_not_enogh_pasc));
                    return;
                }
                else {
                    int dest=Integer.parseInt(destinationAccount.getText().toString());
                    if (accountDest==null || !accountDest.getAccount().equals(dest))
                        checkAccountDest(dest);
                    else
                        prepareSend();
                }
            }
        });
        return result;
    }



    public void setPascalcoinServiceProvider(PascalcoinServiceProvider pascalcoinServiceProvider) {
        this.pascalcoinServiceProvider=pascalcoinServiceProvider;
    }

    private void checkAccountDest(Integer accountDest) {
        pascalcoinServiceProvider.getAccount(accountDest);
    }

    public void prepareSend() {
        Bundle results = new Bundle();
        results.putInt("originAccount", accountFrom.getAccount());
        results.putInt("destinationAccount", Integer.parseInt(destinationAccount.getText().toString()));
        results.putDouble("amount", getAmount());
        results.putDouble("fee", getFee());
        if (transferPayload.getVisibility()==View.VISIBLE) {
            results.putString("payload",transferPayload.getText().toString());
            results.putString("payloadEncription", getResources().getStringArray(R.array.payloadEncryptionMethodValue)[encriptionMethod.getSelectedItemPosition()]);
            results.putString("payloadPassword", encryptionPassword.getText().toString());
        }
        onButtonPressed(results, accountFrom,accountDest);
        getDialog().dismiss();
    }

    private boolean balanceOk() {
        Double transfer =getAmount();
        Double fee = 0.0;
        if (!transferFee.getText().toString().isEmpty()) {
            fee= getFee();
        }
        return accountFrom.getBalance()>=(transfer+fee);
    }

    private Double getAmount() {
        if (transferAmount.getText().toString().isEmpty()) return 0.0;
        Double result=0.0;
        try {
            result = Double.parseDouble(transferAmount.getText().toString().replace(',', '.'));;
        } catch(NumberFormatException pe) {
            Log.e(this.getTag(),pe.getMessage());
        }
        return result;
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
            encryptionPassword.setVisibility( encriptionMethod.getSelectedItemPosition()==3?View.VISIBLE:View.GONE);
        } else {
            transferPayload.setVisibility(View.GONE);
            encriptionMethod.setVisibility(View.GONE);
            encryptionPassword.setVisibility(View.GONE);
        }

    }

    public void onButtonPressed(Bundle uri, Account from,Account dest) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri,from,dest);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentSendPascListener) {
            mListener = (OnFragmentSendPascListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentSendPascListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setAccountFrom(Account accountFrom) {
        this.accountFrom = accountFrom;
    }

    public void onAccountSelected(Account account, Boolean sale) {
        accountDest=account;
        destinationAccount.setText(""+account.getAccount());
        destinationAccountChecksum.setText(""+ PascalUtils.calculateChecksum(account.getAccount()));
        Fragment search=getActivity().getSupportFragmentManager().findFragmentByTag("search_accounts");
        if (search!=null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(search).commit();
        } else {
            prepareSend();
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentSendPascListener {
        void onFragmentInteraction(Bundle params, final Account accountFrom, final Account accountTo);
    }
}
