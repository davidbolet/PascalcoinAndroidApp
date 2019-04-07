package org.pascalcoin.pascalcoinofficial.controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.davidbolet.jpascalcoin.api.client.PascalCoinClient;
import com.github.davidbolet.jpascalcoin.api.client.PascalCoinClientImpl;
import com.github.davidbolet.jpascalcoin.api.model.NodeStatus;
import com.github.davidbolet.jpascalcoin.api.model.OpResult;

import org.pascalcoin.pascalcoinofficial.R;
import org.pascalcoin.pascalcoinofficial.model.NodeInfo;
import org.pascalcoin.pascalcoinofficial.services.PreferencesService;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NodeAddFragment extends DialogFragment {
    private static final String TAG = NodeAddFragment.class.getSimpleName();

    private PreferencesService preferencesService;
    private Button buttonClose;
    private Button buttonSave;
    private ImageButton buttonCheck;
    private TextView txtNodeAddress;
    private TextView txtNodeName;
    private TextView txtAddNodeTitle;
    private TextView txtCheckUrl;
    private TextView txtResultCheck;
    private List<NodeInfo> existingNodes;
    private NodeInfo nodeInfo;
    private ImageView imgResultOk;
    private ImageView imgResultKo;
    public NodeAddFragment() {}

    public static NodeAddFragment newInstance(PreferencesService preferencesService,List<NodeInfo>  existingNodes,NodeInfo nodeInfo) {
        NodeAddFragment fragment = new NodeAddFragment();
        fragment.preferencesService=preferencesService;
        fragment.nodeInfo=nodeInfo;
        fragment.existingNodes=existingNodes;
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
        txtAddNodeTitle=result.findViewById(R.id.title_add_node);
        txtNodeAddress=result.findViewById(R.id.txt_node_uri);
        txtNodeName=result.findViewById(R.id.txt_node_name);
        txtCheckUrl=result.findViewById(R.id.txt_check_url);
        txtResultCheck=result.findViewById(R.id.result_text);
        imgResultOk=result.findViewById(R.id.result_ok);
        imgResultKo=result.findViewById(R.id.result_ko);
        txtResultCheck.setVisibility(View.INVISIBLE);
        imgResultOk.setVisibility(View.INVISIBLE);
        imgResultKo.setVisibility(View.INVISIBLE);

        if (nodeInfo!=null) {
            txtNodeAddress.setText(nodeInfo.getUrl());
            txtNodeName.setText(nodeInfo.getName());
            txtAddNodeTitle.setText(R.string.txt_edit_node);
        }
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
                txtResultCheck.setVisibility(View.INVISIBLE);
                imgResultOk.setVisibility(View.INVISIBLE);
                imgResultKo.setVisibility(View.INVISIBLE);
                if (txtNodeAddress.getText().toString().isEmpty()) {
                    txtNodeAddress.setError(getText(R.string.error_node_url_requiered));
                    return;
                }
                String url=txtNodeAddress.getText().toString();
                if (!URLUtil.isValidUrl(url)) {
                    txtNodeAddress.setError(getText(R.string.error_invalid_url));
                    return;
                }
                try {
                    PascalCoinClient pascalCoinClient = new PascalCoinClientImpl(url, 0);
                    buttonCheck.setEnabled(false);
                    Callback<OpResult<NodeStatus>> opResultCallback = new Callback<OpResult<NodeStatus>>() {
                        @Override
                        public void onResponse(Call<OpResult<NodeStatus>> call, Response<OpResult<NodeStatus>> response) {
                            try {
                                txtResultCheck.setVisibility(View.VISIBLE);
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        NodeStatus resp = response.body().getResult();

                                        imgResultOk.setVisibility(View.VISIBLE);
                                        imgResultKo.setVisibility(View.INVISIBLE);
                                        txtResultCheck.setText( "Node Status:" + resp.getStatusDescriptor());

                                    } else {
                                        imgResultOk.setVisibility(View.INVISIBLE);
                                        imgResultKo.setVisibility(View.VISIBLE);
                                        txtResultCheck.setText(getString(R.string.txt_error_connect_200_empty_body));

                                    }
                                } else {
                                    imgResultOk.setVisibility(View.INVISIBLE);
                                    imgResultKo.setVisibility(View.VISIBLE);
                                    txtResultCheck.setText(getString(R.string.error_contacting_node) + (response.errorBody() == null ? "" : response.errorBody().string()));
                                }
                            } catch (Exception e) {
                                Log.e("NOD", e.getMessage());
                            }
                            buttonCheck.setEnabled(true);
                        }

                        @Override
                        public void onFailure(Call<OpResult<NodeStatus>> call, Throwable t) {
                            txtResultCheck.setVisibility(View.VISIBLE);
                            imgResultKo.setVisibility(View.VISIBLE);
                            txtResultCheck.setText(getString(R.string.error_contacting_node) + t.getMessage());
                            buttonCheck.setEnabled(true);
                        }
                    };
                    pascalCoinClient.getNodeStatusAsync(opResultCallback);
                } catch(Exception ex) {
                    Log.e("NOD", ex.getMessage());
                }
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
                if ((nodeInfo==null || !nodeInfo.getName().equals(txtNodeName.getText().toString()))  && existsName(txtNodeName.getText().toString())) {
                    txtNodeName.setError(getText(R.string.error_node_name_already_exist));
                    return;
                }
                if (nodeInfo==null) {
                    nodeInfo = new NodeInfo(txtNodeName.getText().toString(), txtNodeAddress.getText().toString(), txtNodeAddress.toString().startsWith("https://"));
                    preferencesService.addNodeInfo(nodeInfo);
                }
                else {
                    nodeInfo.setName(txtNodeName.getText().toString());
                    nodeInfo.setUrl(txtNodeAddress.getText().toString());
                    nodeInfo.setSSL(txtNodeAddress.toString().startsWith("https://"));
                    preferencesService.updateNodeInfo(nodeInfo);
                }
                txtNodeAddress.setText("");
                txtNodeName.setText("");
                nodeInfo=null;
                Toast.makeText(getContext(), getText(R.string.msg_node_saved), Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });
        return result;
    }

    private boolean existsName(String name) {
        for(NodeInfo ni:existingNodes) {
            if (ni.getName().equals(name))
                return true;
        }
        return false;
    }

}
