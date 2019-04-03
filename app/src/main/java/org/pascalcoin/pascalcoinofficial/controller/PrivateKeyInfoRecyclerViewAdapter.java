package org.pascalcoin.pascalcoinofficial.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.davidbolet.jpascalcoin.api.model.Operation;
import com.github.davidbolet.jpascalcoin.common.model.PascPublicKey;

import org.pascalcoin.pascalcoinofficial.R;
import org.pascalcoin.pascalcoinofficial.model.PrivateKeyInfo;

import java.util.List;

import static org.pascalcoin.pascalcoinofficial.controller.NodeListFragment.OnNodeListFragmentInteractionListener.OPERATION_NODE_DELETE;
import static org.pascalcoin.pascalcoinofficial.controller.NodeListFragment.OnNodeListFragmentInteractionListener.OPERATION_NODE_EDIT;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Operation} and makes a call to the
 * specified {@link OperationListFragment.OnListOperationFragmentInteractionListener}.
 */
public class PrivateKeyInfoRecyclerViewAdapter extends RecyclerView.Adapter<PrivateKeyInfoRecyclerViewAdapter.KeyInfoViewHolder> {


    private List<PrivateKeyInfo> mValues;
    private PascPublicKey mCurrentKey;
    private final PrivateKeyListFragment.OnKeyListFragmentInteractionListener mListener;

    public PrivateKeyInfoRecyclerViewAdapter(List<PrivateKeyInfo> items, PascPublicKey currentNode, PrivateKeyListFragment.OnKeyListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mCurrentKey =currentNode;
    }

    @Override
    public KeyInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.keyinfo_list_content, parent, false);
        return new KeyInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final KeyInfoViewHolder holder, int position) {

         holder.mItem = mValues.get(position);
         if (holder.mItem.isEncrypted()) {
             holder.encrypted.setImageResource(R.drawable.ic_lock);
         } else {
             holder.encrypted.setImageResource(R.drawable.ic_lock_open);
         }
         holder.keyName.setText("" + mValues.get(position).getName());
         holder.keyType.setText(mValues.get(position).getPascKeyType().name());
         if (holder.mItem.getPublicKey().equals(mCurrentKey.getEncPubKey())) {
             holder.deleteNode.setVisibility(View.INVISIBLE);
             holder.editNode.setVisibility(View.INVISIBLE);
         }
         else {
             holder.editNode.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if (null != mListener) {
                         mListener.onKeySelectedInteraction(holder.mItem, OPERATION_NODE_EDIT);
                     }
                 }
             });
             holder.deleteNode.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if (null != mListener) {
                         mListener.onKeySelectedInteraction(holder.mItem, OPERATION_NODE_DELETE);
                     }
                 }
             });
         }
     }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    class KeyInfoViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView encrypted;
        public final TextView keyName;
        public final TextView keyType;
        public final ImageView editNode;
        public final ImageView deleteNode;
        public PrivateKeyInfo mItem;

        public KeyInfoViewHolder(View view) {
            super(view);
            mView = view;
            encrypted = view.findViewById(R.id.key_encrypted);
            keyName = view.findViewById(R.id.key_name);
            keyType = view.findViewById(R.id.key_type);
            editNode = view.findViewById(R.id.edit_key);
            deleteNode= view.findViewById(R.id.delete_key);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + keyName.getText() + "'";
        }
    }
}
