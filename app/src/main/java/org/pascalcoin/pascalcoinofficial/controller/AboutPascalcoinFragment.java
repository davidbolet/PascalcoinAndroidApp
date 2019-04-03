package org.pascalcoin.pascalcoinofficial.controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.pascalcoin.pascalcoinofficial.R;

public class AboutPascalcoinFragment extends DialogFragment {
    private static final String TAG = AboutPascalcoinFragment.class.getSimpleName();

    private Button buttonClose;
    private TextView txtReceiveAddress;

    public AboutPascalcoinFragment() {}

    public static AboutPascalcoinFragment newInstance() {
        AboutPascalcoinFragment fragment = new AboutPascalcoinFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_pascal_info, container, false);
        TextView linkWeb=result.findViewById(R.id.link_pascalcoin);
        linkWeb.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

        TextView linkDisc=result.findViewById(R.id.link_pascalcoin_discord);
        linkDisc.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

        TextView linkTW=result.findViewById(R.id.link_pascalcoin_twitter);
        linkTW.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

        TextView linkFB=result.findViewById(R.id.link_pascalcoin_facebook);
        linkFB.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());

        buttonClose = result.findViewById(R.id.buttonCancel);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return result;
    }

}
