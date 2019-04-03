package org.pascalcoin.pascalcoinofficial.controller;

import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.pascalcoin.pascalcoinofficial.AccountActivity;
import org.pascalcoin.pascalcoinofficial.R;
import org.pascalcoin.pascalcoinofficial.helper.PascalUtils;

import com.github.davidbolet.jpascalcoin.api.model.Account;
import com.github.davidbolet.jpascalcoin.api.model.AccountState;
import com.github.davidbolet.jpascalcoin.api.model.Operation;

import java.math.BigDecimal;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Operation} and makes a call to the
 * specified {@link OperationListFragment.OnListOperationFragmentInteractionListener}.
 */
public class SearchAccountRecyclerViewAdapter extends RecyclerView.Adapter<SearchAccountRecyclerViewAdapter.ViewHolder> {

    private List<Account> mValues;
    private Integer numColumns;
    private Boolean showSend;
    private final SearchAccountFragment.OnAccountSearchInteraction mListener;

    public SearchAccountRecyclerViewAdapter(List<Account> items, Integer numColumns, Boolean showSend, SearchAccountFragment.OnAccountSearchInteraction listener) {
        mValues = items;
        mListener = listener;
        this.numColumns=numColumns;
        this.showSend=showSend;
    }

    View.OnClickListener getAccountSelectedListener(final Account account, final Boolean sale, Boolean send) {
        View.OnClickListener result;
        if (send) {
            result = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener && mListener instanceof MyAccountsFragment.OnFragmentInteractionListener) {
                        ((MyAccountsFragment.OnFragmentInteractionListener) mListener).onFragmentInteraction(account, AccountActivity.OP_SEND_DEST);
                    }
                }
            };
        } else {
            result = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onAccountSelected(account, sale);
                    }
                }
            };
        }
        return result;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_account_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mAccNumberView.setText(""+mValues.get(position).getAccount()+"-"+ PascalUtils.calculateChecksum(mValues.get(position).getAccount()));

        holder.mAccNumberView.setOnClickListener(getAccountSelectedListener(holder.mItem, false, false));

        holder.mAccNameView.setText(mValues.get(position).getName());
        holder.mAccNameView.setOnClickListener(getAccountSelectedListener(holder.mItem,false,false));
        if (mValues.get(position).getBalance()==0.0) {
            holder.mAccBalanceView.setText("0 PASC");
        } else {
            holder.mAccBalanceView.setText(String.format("%.4f PASC", mValues.get(position).getBalance()));
        }
        holder.mAccBalanceView.setOnClickListener(getAccountSelectedListener(holder.mItem, false, false));
        if (numColumns==3)
        {
            holder.mAccSale.setVisibility(View.GONE);
            holder.mAccSalePrice.setVisibility(View.GONE);
        }
        else
        {
            boolean check=holder.mItem.getState().equals(AccountState.LISTED);
            if (check) {
                android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 25);
                holder.mAccNumberView.setLayoutParams(params);
                if ( mValues.get(position).getName()==null || "".equals(mValues.get(position).getName())) {
                    holder.mAccNameView.setVisibility(View.GONE);
                } else {
                    holder.mAccNameView.setVisibility(View.VISIBLE);
                    android.widget.LinearLayout.LayoutParams params2 = new android.widget.LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 10);
                    holder.mAccNameView.setLayoutParams(params2);
                    holder.mAccBalanceView.setLayoutParams(params2);
                }
                holder.mAccSale.setVisibility(View.VISIBLE);
                holder.sendPascButton.setVisibility(View.GONE);
                holder.mAccSalePrice.setVisibility(View.VISIBLE);
                if (isFraction0(mValues.get(position).getPrice())) {
                    holder.mAccSalePrice.setText(String.format("(%.1f)", mValues.get(position).getPrice()));
                } else
                    holder.mAccSalePrice.setText(String.format("(%.4f)", mValues.get(position).getPrice()));
                if (holder.mItem.getPrivateSale()) {
                    holder.mAccSale.setImageResource(R.drawable.ic_lock);
                }
                else {
                    holder.mAccSale.setOnClickListener(getAccountSelectedListener(holder.mItem, true,false));
                    holder.mAccSalePrice.setOnClickListener(getAccountSelectedListener(holder.mItem, true, false));
                }
            }
            else {
                holder.mAccSale.setVisibility(View.GONE);
                holder.mAccSalePrice.setVisibility(View.GONE);
                android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 25);
                holder.mAccNumberView.setLayoutParams(params);
                holder.mAccNameView.setVisibility(View.VISIBLE);
                android.widget.LinearLayout.LayoutParams params2 = new android.widget.LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 30);
                holder.mAccNameView.setLayoutParams(params2);
                android.widget.LinearLayout.LayoutParams params3 = new android.widget.LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 35);
                holder.mAccBalanceView.setLayoutParams(params3);
                android.widget.LinearLayout.LayoutParams params4 = new android.widget.LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT, 10);
                holder.sendPascButton.setLayoutParams(params4);
                if (this.showSend)
                    holder.sendPascButton.setVisibility(View.VISIBLE);
                else
                    holder.sendPascButton.setVisibility(View.INVISIBLE);
                holder.sendPascButton.setOnClickListener(getAccountSelectedListener(holder.mItem,false,true));
            }
        }
    }

    public void setmValues(List<Account> values) {
        this.mValues=values;
    }

    @Override
    public int getItemCount() {
        return mValues==null?0:mValues.size();
    }

    private boolean isFraction0(Double num) {
        BigDecimal number = new BigDecimal(num);
        return (number.remainder(BigDecimal.ONE)).doubleValue()<0.0001;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAccNumberView;
        public final TextView mAccNameView;
        public final TextView mAccBalanceView;
        public final ImageView mAccSale;
        public final TextView mAccSalePrice;
        public final ImageButton sendPascButton;
        public Account mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAccNumberView = view.findViewById(R.id.account_number);
            mAccNameView = view.findViewById(R.id.account_name);
            mAccBalanceView = view.findViewById(R.id.account_balance);
            mAccSale= view.findViewById(R.id.checkBoxIsSale);
            mAccSalePrice= view.findViewById(R.id.account_sale_price);
            sendPascButton= view.findViewById(R.id.imageButtonSendPasc);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAccNumberView.getText() + "'";
        }
    }
}
