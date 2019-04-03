package org.pascalcoin.pascalcoinofficial.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.davidbolet.jpascalcoin.api.model.Operation;

import org.pascalcoin.pascalcoinofficial.R;
import org.pascalcoin.pascalcoinofficial.model.NodeInfo;

import java.util.List;

import static org.pascalcoin.pascalcoinofficial.controller.NodeListFragment.OnNodeListFragmentInteractionListener.OPERATION_NODE_DELETE;
import static org.pascalcoin.pascalcoinofficial.controller.NodeListFragment.OnNodeListFragmentInteractionListener.OPERATION_NODE_EDIT;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Operation} and makes a call to the
 * specified {@link OperationListFragment.OnListOperationFragmentInteractionListener}.
 */
public class NodeInfoRecyclerViewAdapter extends RecyclerView.Adapter<NodeInfoRecyclerViewAdapter.NodeInfoViewHolder> {


    private List<NodeInfo> mValues;
    private NodeInfo mCurrentNode;
    private final NodeListFragment.OnNodeListFragmentInteractionListener mListener;

    public NodeInfoRecyclerViewAdapter(List<NodeInfo> items, NodeInfo currentNode, NodeListFragment.OnNodeListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        mCurrentNode =currentNode;
    }

    @Override
    public NodeInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nodeinfo_list_content, parent, false);
        return new NodeInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NodeInfoViewHolder holder, int position) {

         holder.mItem = mValues.get(position);
         holder.nodeName.setText("" + mValues.get(position).getName());
         holder.nodeUrl.setText(mValues.get(position).getUrl());
         if (holder.mItem.equals(mCurrentNode)) {
             holder.deleteNode.setVisibility(View.INVISIBLE);
             holder.editNode.setVisibility(View.INVISIBLE);
         }
         else {
             holder.editNode.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if (null != mListener) {
                         mListener.onNodeSelectedInteraction(holder.mItem, OPERATION_NODE_EDIT);
                     }
                 }
             });
             holder.deleteNode.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if (null != mListener) {
                         mListener.onNodeSelectedInteraction(holder.mItem, OPERATION_NODE_DELETE);
                     }
                 }
             });
         }
     }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    class NodeInfoViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView nodeName;
        public final TextView nodeUrl;
        public final ImageView editNode;
        public final ImageView deleteNode;
        public NodeInfo mItem;

        public NodeInfoViewHolder(View view) {
            super(view);
            mView = view;
            nodeName = view.findViewById(R.id.node_name);
            nodeUrl = view.findViewById(R.id.node_url);
            editNode = view.findViewById(R.id.edit_node);
            deleteNode= view.findViewById(R.id.delete_node);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nodeName.getText() + "'";
        }
    }
}
