package org.pascalcoin.pascalcoinofficial.controller;

import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.EditText;

import com.github.davidbolet.jpascalcoin.api.model.Account;
import java.util.List;

public abstract class OperationFragment extends DialogFragment {

    protected List<Account> userAccounts;
    protected EditText transferFee;

    public void setUserAccounts(List<Account> userAccounts) {
        this.userAccounts=userAccounts;
    }

    protected Double getFee() {
        if (transferFee.getText().toString().isEmpty()) return 0.0;
        Double result=0.0;
        try {
            result = Double.parseDouble(transferFee.getText().toString().replace(',', '.'));
        } catch (NumberFormatException e) {
            Log.e(this.getTag(),e.getMessage());
        }
        return result;
    }
}
