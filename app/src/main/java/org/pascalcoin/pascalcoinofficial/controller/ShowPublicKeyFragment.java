package org.pascalcoin.pascalcoinofficial.controller;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.pascalcoin.pascalcoinofficial.R;
import org.pascalcoin.pascalcoinofficial.helper.QRUtils;
import com.google.zxing.WriterException;

public class ShowPublicKeyFragment extends DialogFragment {
    private static final String TAG = ShowPublicKeyFragment.class.getSimpleName();

    private Button buttonClose;
    private ImageView imgReceiveQR;
    private TextView txtReceiveAddress;
    private TextView txtAddress;
    private boolean isPrivate;
    private boolean isEncrypted;

    private String publicKey;

    public ShowPublicKeyFragment() {}

    public static ShowPublicKeyFragment newInstance(String pubKey) {
        return newInstance( pubKey, false, false);
    }

    public static ShowPublicKeyFragment newInstance(String pubKey, boolean isPrivate, boolean isEncrypted) {
        ShowPublicKeyFragment fragment = new ShowPublicKeyFragment();
        fragment.setPublicKey(pubKey);
        fragment.isPrivate=isPrivate;
        fragment.isEncrypted=isEncrypted;
        return fragment;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_show_public_key, container, false);
        buttonClose = result.findViewById(R.id.buttonCancel);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        imgReceiveQR = result.findViewById(R.id.imageReceiveQR);
        txtReceiveAddress = result.findViewById(R.id.strTxtReceiveAddressValue);
        txtReceiveAddress.setText(publicKey);
        txtAddress= result.findViewById(R.id.strTxtAddress);
        if (isPrivate) {
            if (isEncrypted)
                txtAddress.setText(R.string.private_key_encrypted_value);
            else
                txtAddress.setText(R.string.private_key_raw_value);
        }
        if (publicKey!=null) {
            try {
                imgReceiveQR.setImageBitmap(QRUtils.encodeAsBitmap(publicKey, 700));
            } catch (WriterException e) {
                Log.e(TAG, e.getMessage());
            }
            //We copy also key to keyboard
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(isPrivate?"private_key":"public_key", publicKey);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), R.string.txt_pk_clipboard, Toast.LENGTH_SHORT).show();
        }
        return result;
    }

}
