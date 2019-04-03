package org.pascalcoin.pascalcoinofficial.controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
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
import org.pascalcoin.pascalcoinofficial.helper.PascalUtils;
import org.pascalcoin.pascalcoinofficial.model.PrivateKeyInfo;
import org.pascalcoin.pascalcoinofficial.services.PreferencesService;

public class EditPascalcoinKeyFragment extends DialogFragment {
    private static final String TAG = EditPascalcoinKeyFragment.class.getSimpleName();

    private PreferencesService preferencesService;
    private Button buttonClose;
    private Button buttonSave;
    private Button buttonExport;
    private EditText txtKeyName;
    private TextView txtKeyValue;
    private EditText txtKeyPassword;
    private Switch switchEncrypt;
    private PrivateKeyInfo privateKeyInfo;
    private String password;

    public EditPascalcoinKeyFragment() {
    }


    public static EditPascalcoinKeyFragment newInstance(PreferencesService preferencesService, PrivateKeyInfo keyInfo, String password) {
        EditPascalcoinKeyFragment fragment = new EditPascalcoinKeyFragment();
        fragment.preferencesService=preferencesService;
        fragment.privateKeyInfo=keyInfo;
        fragment.password=password;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.edit_key_form, container, false);

        txtKeyValue=result.findViewById(R.id.txt_key_value);
        txtKeyValue.setText(this.privateKeyInfo.getPrivateKey());
        txtKeyName=result.findViewById(R.id.txt_key_name);
        txtKeyName.setText(privateKeyInfo.getName());
        txtKeyPassword=result.findViewById(R.id.txt_key_password);
        txtKeyPassword.setText(this.password);
        buttonClose = result.findViewById(R.id.btn_new_key_cancel);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        switchEncrypt = result.findViewById(R.id.switch_encrypt);
        switchEncrypt.setChecked(privateKeyInfo.isEncrypted());
        switchEncrypt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    txtKeyPassword.setVisibility(View.VISIBLE);
                }
                else
                {
                    txtKeyPassword.setText("");
                    txtKeyPassword.setVisibility(View.GONE);
                }
            }
        });

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

                privateKeyInfo.setName(txtKeyName.getText().toString());
                privateKeyInfo.setEncrypted(switchEncrypt.isChecked());
                if (switchEncrypt.isChecked()) {
                    try {
                        String password = txtKeyPassword.getText().toString().trim();
                        privateKeyInfo.setPrivateKey(OpenSslAes.encrypt(password, privateKeyInfo.getPrivateKey()));
                    } catch(Exception ex) {}
                } else {
                    privateKeyInfo.setPrivateKey(privateKeyInfo.getPrivateKey());
                }
                preferencesService.updatePrivateKey(privateKeyInfo);
                Toast.makeText(getContext(), getText(R.string.msg_key_saved), Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });


        buttonExport = result.findViewById(R.id.button_export);
        buttonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchEncrypt.isChecked()) {
                    if (txtKeyPassword.getText().toString().trim().isEmpty() ) {
                        txtKeyPassword.setError(getText(R.string.error_key_password_required));
                        return;
                    }
                }
                String password=txtKeyPassword.getText().toString().trim();
                String toShow=privateKeyInfo.getPrivateKey();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                Fragment frag = manager.findFragmentByTag("fragment_show_pk");
                if (frag != null) {
                    manager.beginTransaction().remove(frag).commit();
                }
                if (privateKeyInfo.isEncrypted()) {
                    try {
                        toShow = HexConversionsHelper.int2BigEndianHex(privateKeyInfo.getKeyType())+privateKeyInfo.getKeyType()+ OpenSslAes.encrypt(password, toShow);
                    } catch (Exception ex) {
                        Log.e("EditPascalcoinKeyFrag", ex.getMessage());
                    }
                }
                ShowPublicKeyFragment pkFragment = ShowPublicKeyFragment.newInstance(toShow, true,privateKeyInfo.isEncrypted());
                pkFragment.show(manager, "fragment_show_pk");
            }
        });


        return result;
    }

}
