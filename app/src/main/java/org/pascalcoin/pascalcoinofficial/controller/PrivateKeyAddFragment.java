package org.pascalcoin.pascalcoinofficial.controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutCompat;
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

import org.pascalcoin.pascalcoinofficial.R;
import org.pascalcoin.pascalcoinofficial.model.PrivateKeyInfo;
import org.pascalcoin.pascalcoinofficial.services.PreferencesService;

public class PrivateKeyAddFragment extends DialogFragment {
    private static final String TAG = PrivateKeyAddFragment.class.getSimpleName();

    private PreferencesService preferencesService;
    private Button buttonClose;
    private Button buttonSave;
    private Button buttonGenerate;
    private Spinner spinnerKeyType;
    private EditText txtKeyName;
    private TextView txtKeyValue;
    private EditText txtKeyPassword;
    private EditText txtImportKey;
    private Switch switchEncrypt;
    private Switch switchImport;
    private LinearLayoutCompat createKeyLayout1;
    private LinearLayoutCompat createKeyLayout2;
    private PascPrivateKey lastGenerated;

    public PrivateKeyAddFragment() {
    }


    public static PrivateKeyAddFragment newInstance(PreferencesService preferencesService) {
        PrivateKeyAddFragment fragment = new PrivateKeyAddFragment();
        fragment.preferencesService=preferencesService;
        return fragment;
    }

    public void setGeneratedPK(PascPrivateKey key) {
        lastGenerated=key;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.new_key_form, container, false);

        createKeyLayout1 =result.findViewById(R.id.layout_create_key_1);
        createKeyLayout2 =result.findViewById(R.id.layout_create_key_2);
        txtKeyValue=result.findViewById(R.id.txt_key_value);
        txtKeyName=result.findViewById(R.id.txt_key_name);
        txtKeyPassword=result.findViewById(R.id.txt_key_password);

        buttonClose = result.findViewById(R.id.btn_new_key_cancel);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        txtImportKey = result.findViewById(R.id.txt_import_key);
        switchEncrypt = result.findViewById(R.id.switch_encrypt);
        switchEncrypt.setChecked(true);
        switchEncrypt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    txtKeyPassword.setVisibility(View.VISIBLE);
                    txtImportKey.setHint(R.string.private_key_import_encrypted);
                    createKeyLayout2.setVisibility(View.GONE);
                }
                else
                {
                    txtKeyPassword.setText("");
                    txtKeyPassword.setVisibility(View.GONE);
                    createKeyLayout2.setVisibility(View.VISIBLE);
                    txtImportKey.setHint(R.string.private_key_import);
                }
            }
        });

        spinnerKeyType= result.findViewById(R.id.key_type_spinner);
        String[] arrayItems = { "SECP256K1","SECP384R1","SECT283K1","SECP521R1"};


        ArrayAdapter<String> adapter= new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, arrayItems);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerKeyType.setAdapter(adapter);

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
                if (txtImportKey.getVisibility()==View.VISIBLE && txtImportKey.getText().toString().isEmpty()) {
                    txtImportKey.setError(getText(R.string.error_private_key_not_typed));
                    return;
                }

                PascPrivateKey key;
                KeyType keyType;
                keyType=lastGenerated.getKeyType();
                key=lastGenerated;
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


        buttonGenerate = result.findViewById(R.id.button_generate);
        buttonGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    KeyType selectedType=KeyType.values()[spinnerKeyType.getSelectedItemPosition()];
                    PascPrivateKey key =PascPrivateKey.generate(selectedType);
                    setGeneratedPK( key);
                    txtKeyValue.setText(key.getPrivateKey());
                    buttonGenerate.setVisibility(View.INVISIBLE);
                    buttonSave.setEnabled(true);
                } catch (UnsupportedKeyTypeException e) {
                }
            }
        });


        return result;
    }

}
