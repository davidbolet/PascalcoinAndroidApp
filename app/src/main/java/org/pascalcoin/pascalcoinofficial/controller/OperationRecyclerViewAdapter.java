package org.pascalcoin.pascalcoinofficial.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pascalcoin.pascalcoinofficial.R;
import com.github.davidbolet.jpascalcoin.api.model.Operation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Operation} and makes a call to the
 * specified {@link OperationListFragment.OnListOperationFragmentInteractionListener}.
 */
public class OperationRecyclerViewAdapter extends RecyclerView.Adapter<OperationRecyclerViewAdapter.ViewHolder> {


    private final List<Operation> mValues;
    private final OperationListFragment.OnListOperationFragmentInteractionListener mListener;

    public OperationRecyclerViewAdapter(List<Operation> items, OperationListFragment.OnListOperationFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public OperationRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =null;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_operation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OperationRecyclerViewAdapter.ViewHolder holder, int position) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            holder.mItem = mValues.get(position);
            holder.mIdView.setText("" + mValues.get(position).getNOperation());
            //holder.dateView.setText(df.format(new Date(mValues.get(position).getTime())));
            holder.mContentView.setText(mValues.get(position).getTypeDescriptor());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onOperationSelectedInteraction(holder.mItem);
                    }
                }
            });
        }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        //public final TextView dateView;
        public final TextView mContentView;
        public Operation mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            //dateView= view.findViewById(R.id.item_date);
            mContentView = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
