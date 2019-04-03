package org.pascalcoin.pascalcoinofficial.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.pascalcoin.pascalcoinofficial.R;
import com.github.davidbolet.jpascalcoin.api.model.DecryptedPayload;
import com.github.davidbolet.jpascalcoin.api.model.Operation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class OperationDetailFragment extends DialogFragment {

    private Operation operation;
    private DecryptedPayload decryptedPayload=null;
    private Button buttonClose;

    public static OperationDetailFragment newInstance(Operation operation, DecryptedPayload decryptedPayload) {
        OperationDetailFragment operationDetailFragment=new OperationDetailFragment();
        operationDetailFragment.setOperation(operation);
        operationDetailFragment.setDecryptedPayload(decryptedPayload);
        return operationDetailFragment;
    }

    public OperationDetailFragment() {
        super();
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public void setDecryptedPayload(DecryptedPayload decryptedPayload) {
        this.decryptedPayload = decryptedPayload;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        SimpleDateFormat sd= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        View view = inflater.inflate(R.layout.fragment_operation_detail,container, false);
        EditText editTextOpHash=view.findViewById(R.id.editTextOpHash);
        EditText editTextPayload=view.findViewById(R.id.editTextPayload);
        EditText editTextDecodedPayload=view.findViewById(R.id.editTextDecodedPayload);
        editTextOpHash.setEnabled(false);
        TextView textViewBlock=view.findViewById(R.id.textViewBlock);
        TextView textViewDateTime=view.findViewById(R.id.textViewDateTime);
        TextView textViewSender=view.findViewById(R.id.textViewSender);
        TextView textViewAmount=view.findViewById(R.id.textViewAmount);
        TextView textViewReceiver=view.findViewById(R.id.textViewReceiver);
        TextView textViewOperation=view.findViewById(R.id.textViewOperation);

        editTextOpHash.setText(operation.getOpHash());
        if (operation.getBlock() !=null && operation.getOperationBlock()!=null) {
            textViewBlock.setText(String.format("%s: %d/%d", getText(R.string.block_op), operation.getBlock(), operation.getOperationBlock() + 1));
        } else {
            textViewBlock.setText(String.format("%s: %d/%d", getText(R.string.block_op),0, 0));
        }
        if (operation.getTime()!=null) {
            Date gmt = new Date(operation.getTime() * 1000);
            Log.d("OperationDetailFragment", TimeZone.getDefault().getDisplayName());
            gmt.setTime(gmt.getTime() + TimeZone.getTimeZone("GMT+2").getRawOffset());

            textViewDateTime.setText(String.format("%s:%s", getText(R.string.datetime), sd.format(gmt)));
        } else
            textViewDateTime.setText("(pending)");
        textViewAmount.setText(String.format("%s:%.4f PASC",getText(R.string.amount),operation.getAmount()));
        if (operation.getSenderAccount()!=null) {
            textViewSender.setText(String.format("%s:%d", getText(R.string.sender), operation.getSenderAccount()));
        }
        else {
            textViewSender.setText(String.format("%s:%d", getText(R.string.sender), operation.getAccount()));
        }
        if (operation.getDestAccount()!=null) {
            textViewReceiver.setText(String.format("%s:%d",getText(R.string.receiver),operation.getDestAccount()));
        }
        else
            textViewReceiver.setVisibility(View.INVISIBLE);
        textViewOperation.setText(operation.getTypeDescriptor());
        if (operation.getPayLoad()!=null && operation.getPayLoad().length()>2) {
            if (decryptedPayload!=null && Boolean.TRUE.equals(decryptedPayload.getResult())) {
                editTextPayload.setText(decryptedPayload.getUnencryptedPayloadHex());
                editTextDecodedPayload.setText(decryptedPayload.getUnencryptedPayload());
                editTextDecodedPayload.setEnabled(false);
            }
            else {
                editTextDecodedPayload.setEnabled(false);
                if (decryptedPayload!=null && Boolean.FALSE.equals(decryptedPayload.getResult())) {
                    editTextPayload.setText(decryptedPayload.getOriginalPayload());
                    if (decryptedPayload!=null && decryptedPayload.getUnencryptedPayload()!=null) {
                        editTextPayload.setText(decryptedPayload.getUnencryptedPayloadHex());
                        editTextDecodedPayload.setText(decryptedPayload.getUnencryptedPayload());
                    }
                    else {
                        editTextPayload.setText(getText(R.string.could_not_decrypt_payload));
                        editTextDecodedPayload.setVisibility(View.GONE);
                    }
                } else {
                    editTextPayload.setText(getText(R.string.could_not_decrypt_payload));
                    editTextDecodedPayload.setVisibility(View.GONE);
                }
            }
        }
        else {
            editTextPayload.setText(getText(R.string.no_payload));
            editTextDecodedPayload.setVisibility(View.GONE);
        }
        editTextPayload.setEnabled(false);
        buttonClose = view.findViewById(R.id.buttonCancel);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return view;
    }
}
