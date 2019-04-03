package org.pascalcoin.pascalcoinofficial.controller;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.pascalcoin.pascalcoinofficial.R;
import org.pascalcoin.pascalcoinofficial.helper.PascalUtils;

import com.github.davidbolet.jpascalcoin.api.model.Account;
import com.github.davidbolet.jpascalcoin.api.model.AccountState;

import java.util.List;

public class MyAccountsRecyclerViewAdapter extends RecyclerView.Adapter<MyAccountsRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Account> accounts;
    OnItemClickListener onItemClickListener;
    private boolean landscape;

    public MyAccountsRecyclerViewAdapter(Context context, List<Account> accounts, boolean isLanscape) {
        this.context=context;
        this.accounts=accounts;
        this.setLandscape(isLanscape);
    }

    public void setLandscape(boolean landscape) {
        this.landscape = landscape;
    }

    public boolean isLandscape() {
        return landscape;
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.onItemClickListener = listener;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public MyAccountsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view ;
        if (isLandscape())
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.my_account_row_horizontal, parent,false);
        else
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.my_account_row, parent,false);
        return new MyAccountsRecyclerViewAdapter.ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(MyAccountsRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.rowAccountId.setText(String.format("%d-%d",accounts.get(position).getAccount(), PascalUtils.calculateChecksum(accounts.get(position).getAccount())));
        holder.rowAccountName.setText(accounts.get(position).getName()==null?"":accounts.get(position).getName());
        holder.rowAccountBalance.setText(String.format("%.4f PASC",accounts.get(position).getBalance()));
        if (accounts.get(position).getState().equals(AccountState.LISTED)) {
            if (isLandscape()) {
                holder.listAccountForSaleLayer.setVisibility(View.GONE);
                holder.deListAccountForSaleLayer.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (isLandscape()) {
                if (accounts.size()>1) {
                    holder.listAccountForSaleLayer.setVisibility(View.VISIBLE);
                }
                else
                    holder.listAccountForSaleLayer.setVisibility(View.GONE);
                holder.deListAccountForSaleLayer.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return accounts==null?0:accounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView rowAccountId;
        public TextView rowAccountName;
        public TextView rowAccountBalance;


        public ImageButton sendPascButton;
        public ImageButton opsAccountButton;
        public ImageButton sendAccountButton;
        public ImageButton changeAccountButton;
        public ImageButton showOpsButton;

        public ImageButton listForSaleAccountButton;
        public ImageButton delistForSaleAccountButton;

        public LinearLayoutCompat listAccountForSaleLayer;
        public LinearLayoutCompat deListAccountForSaleLayer;

        public long id;

        public ViewHolder(View itemView, final Context ctx) {
            super(itemView);
            context = ctx;
            rowAccountId = itemView.findViewById(R.id.row_account_id);

            rowAccountName = itemView.findViewById(R.id.row_account_name);
            rowAccountBalance = itemView.findViewById(R.id.row_account_balance);

            sendPascButton = itemView.findViewById(R.id.imageButtonSendPasc);
            opsAccountButton = itemView.findViewById(R.id.imageButtonOperations);
            sendAccountButton = itemView.findViewById(R.id.imageButtonSendAccount);
            showOpsButton = itemView.findViewById(R.id.btn_show_ops);
            changeAccountButton = itemView.findViewById(R.id.imageButtonChangeAccount);

            listForSaleAccountButton = itemView.findViewById(R.id.imageButtonListAccount);
            delistForSaleAccountButton = itemView.findViewById(R.id.imageButtonDeListAccount);

            listAccountForSaleLayer = itemView.findViewById(R.id.list_account_layout);
            deListAccountForSaleLayer = itemView.findViewById(R.id.delist_account_layout);

            sendPascButton.setOnClickListener(this);
            opsAccountButton.setOnClickListener(this);
            if (showOpsButton!=null)
                showOpsButton.setOnClickListener(this);
            if (sendAccountButton!=null)
                sendAccountButton.setOnClickListener(this);
            if(changeAccountButton!=null)
                changeAccountButton.setOnClickListener(this);
            if(listForSaleAccountButton!=null)
                listForSaleAccountButton.setOnClickListener(this);
            if(delistForSaleAccountButton!=null)
                delistForSaleAccountButton.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (onItemClickListener!=null)
                onItemClickListener.onItemClick(view,getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}
