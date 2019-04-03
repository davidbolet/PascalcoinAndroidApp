package org.pascalcoin.pascalcoinofficial.controller;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.pascalcoin.pascalcoinofficial.R;

public class AboutPascalcoinAppFragment extends DialogFragment {
    private static final String TAG = AboutPascalcoinAppFragment.class.getSimpleName();

    private Button buttonClose;
    private TextView txtReceiveAddress;

    public AboutPascalcoinAppFragment() {}

    public static AboutPascalcoinAppFragment newInstance() {
        AboutPascalcoinAppFragment fragment = new AboutPascalcoinAppFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_app_info, container, false);
        buttonClose = result.findViewById(R.id.buttonCancel);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            TextView textViewVersion=result.findViewById(R.id.textViewVersion);
            if (textViewVersion!=null)
                textViewVersion.setText(version);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

}
