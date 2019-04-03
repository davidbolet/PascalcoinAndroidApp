package org.pascalcoin.pascalcoinofficial.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.davidbolet.jpascalcoin.common.exception.UnsupportedKeyTypeException;
import com.github.davidbolet.jpascalcoin.common.helper.HexConversionsHelper;
import com.github.davidbolet.jpascalcoin.common.helper.OpenSslAes;
import com.github.davidbolet.jpascalcoin.common.model.KeyType;
import com.github.davidbolet.jpascalcoin.crypto.model.PascPrivateKey;

import org.pascalcoin.pascalcoinofficial.AccountActivity;
import org.pascalcoin.pascalcoinofficial.R;
import org.pascalcoin.pascalcoinofficial.model.PrivateKeyInfo;
import org.pascalcoin.pascalcoinofficial.services.PreferencesService;

public class PrivateKeyImportFragment extends DialogFragment {
    private static final String TAG = PrivateKeyImportFragment.class.getSimpleName();

    static PrivateKeyImportFragment _instance;

    private PreferencesService preferencesService;
    private Button buttonClose;
    private Button buttonSave;
    private Button buttonReadQr;

    private EditText txtKeyName;
    private TextView txtKeyValue;
    private EditText txtKeyPassword;
    private Switch switchEncrypt;


    public PrivateKeyImportFragment() {
    }


    public static PrivateKeyImportFragment newInstance(PreferencesService preferencesService) {
        if (_instance==null) {
            _instance = new PrivateKeyImportFragment();
            _instance.preferencesService = preferencesService;
        }
        if (_instance.preferencesService!=preferencesService) _instance.preferencesService=preferencesService;
        return _instance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.import_key_form, container, false);

        txtKeyValue=result.findViewById(R.id.txt_import_key_value);

        txtKeyValue.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                buttonSave.setEnabled(s.length()>10);
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

        });

        

        txtKeyName=result.findViewById(R.id.txt_import_key_name);
        txtKeyPassword=result.findViewById(R.id.txt_import_key_password);

        buttonClose = result.findViewById(R.id.btn_new_key_cancel);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        switchEncrypt = result.findViewById(R.id.switch_encrypt);
        switchEncrypt.setChecked(true);

        buttonSave = result.findViewById(R.id.btn_new_key_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtKeyName.getText().toString().isEmpty() ) {
                    txtKeyName.setError(getText(R.string.error_key_name_requiered));
                    return;
                }
                if (switchEncrypt.isChecked()) {
                    if (txtKeyPassword.getText().toString().trim().isEmpty() ) {
                        txtKeyPassword.setError(getText(R.string.error_key_password_required));
                        return;
                    }
                }
                if (txtKeyValue.getVisibility()==View.VISIBLE && txtKeyValue.getText().toString().isEmpty()) {
                    txtKeyValue.setError(getText(R.string.error_private_key_not_typed));
                    return;
                }

                PascPrivateKey key;
                KeyType keyType;
                String importedRaw;

                try {
                    importedRaw = OpenSslAes.decrypt(txtKeyPassword.getText().toString(), txtKeyValue.getText().toString());
                    keyType = KeyType.fromValue(HexConversionsHelper.hexBigEndian2Int(importedRaw.substring(0, 4)));
                    key = PascPrivateKey.fromPrivateKey(importedRaw.substring(8), keyType);

                } catch (Exception ex) {
                    txtKeyValue.setError(getString(R.string.error_decrypting_private_key, ex.getMessage()));
                    return;
                }
                PrivateKeyInfo privateKeyInfo=new PrivateKeyInfo();
                privateKeyInfo.setName(txtKeyName.getText().toString());
                privateKeyInfo.setEncrypted(switchEncrypt.isChecked());
                privateKeyInfo.setKeyType(keyType.getValue());
                if (switchEncrypt.isChecked()) {
                    try {
                        String password = txtKeyPassword.getText().toString().trim();
                        privateKeyInfo.setPrivateKey(OpenSslAes.encrypt(password, key.getPrivateKey()));
                    } catch(Exception ex) {}
                } else {
                    privateKeyInfo.setPrivateKey(key.getPrivateKey());
                }
                privateKeyInfo.setPublicKey(key.getPublicKey().getEncPubKey());
                preferencesService.addPrivateKey(privateKeyInfo);
                Toast.makeText(getContext(), getText(R.string.msg_key_saved), Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });

        buttonReadQr = result.findViewById(R.id.button_read_qr);
        buttonReadQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
                    getActivity().startActivityForResult(intent, AccountActivity.SCAN_PRIVATE_KEY);

                } catch (Exception e) {

                    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
                    startActivity(marketIntent);
                }
            }
        });


        return result;
    }

    public void setPrivateKeyValue(String contents) {
        if (txtKeyValue!=null) {
            txtKeyValue.setText(contents);
            buttonSave.setEnabled(true);
        }
        else
            Toast.makeText(getContext(),"Error assigning scan result!", Toast.LENGTH_LONG);
    }
}
