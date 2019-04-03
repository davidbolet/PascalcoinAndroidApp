package org.pascalcoin.pascalcoinofficial.controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.pascalcoin.pascalcoinofficial.R;
import org.pascalcoin.pascalcoinofficial.model.NodeInfo;
import org.pascalcoin.pascalcoinofficial.services.PreferencesService;

public class NodeAddFragment extends DialogFragment {
    private static final String TAG = NodeAddFragment.class.getSimpleName();

    private PreferencesService preferencesService;
    private Button buttonClose;
    private Button buttonSave;
    private ImageButton buttonCheck;
    private TextView txtNodeAddress;
    private TextView txtNodeName;

    public NodeAddFragment() {}

    public static NodeAddFragment newInstance(PreferencesService preferencesService) {
        NodeAddFragment fragment = new NodeAddFragment();
        fragment.preferencesService=preferencesService;
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.new_node_form, container, false);

        txtNodeAddress=result.findViewById(R.id.txt_node_uri);
        txtNodeName=result.findViewById(R.id.txt_node_name);

        buttonClose = result.findViewById(R.id.btn_new_node_cancel);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        buttonCheck = result.findViewById(R.id.btn_new_node_check);
        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonSave = result.findViewById(R.id.btn_new_node_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtNodeName.getText().toString().isEmpty() ) {
                    txtNodeName.setError(getText(R.string.error_node_name_requiered));
                    return;
                }
                if (txtNodeAddress.getText().toString().isEmpty()) {
                    txtNodeAddress.setError(getText(R.string.error_node_url_requiered));
                    return;
                }
                NodeInfo nodeInfo=new NodeInfo(txtNodeName.getText().toString(),txtNodeAddress.getText().toString(),txtNodeAddress.toString().startsWith("https://"));
                if (preferencesService.getNodeInfos().contains(nodeInfo)) {
                    txtNodeName.setError(getText(R.string.error_node_name_already_exist));
                    return;
                }
                preferencesService.addNodeInfo(nodeInfo);
                txtNodeAddress.setText("");
                txtNodeName.setText("");
                Toast.makeText(getContext(), getText(R.string.msg_node_saved), Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });
        return result;
    }

}
