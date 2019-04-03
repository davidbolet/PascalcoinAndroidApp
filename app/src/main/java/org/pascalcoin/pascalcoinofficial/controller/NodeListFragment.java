package org.pascalcoin.pascalcoinofficial.controller;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.pascalcoin.pascalcoinofficial.R;
import org.pascalcoin.pascalcoinofficial.model.NodeInfo;

import java.util.List;

public class NodeListFragment extends Fragment {

    private List<NodeInfo> nodeList;
    private NodeInfo currentNode;
    private OnNodeListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private FloatingActionButton addButton;

    public static NodeListFragment newInstance(List<NodeInfo> nodes, NodeInfo currentNode) {
        NodeListFragment fragment = new NodeListFragment();
        fragment.nodeList=nodes;
        fragment.currentNode=currentNode;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nodeinfo_list, container, false);

        addButton =view.findViewById(R.id.add_node);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener!=null)
                    mListener.onNodeSelectedInteraction(null, OnNodeListFragmentInteractionListener.OPERATION_NODE_ADD);
            }
        });
        recyclerView=view.findViewById(R.id.nodeinfo_list);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(new NodeInfoRecyclerViewAdapter(nodeList, currentNode, mListener));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNodeListFragmentInteractionListener) {
            mListener = (OnNodeListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnNodeListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setNodeList(List<NodeInfo> nodeInfos) {
        this.nodeList=nodeInfos;
        if (recyclerView!=null) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
            recyclerView.setAdapter(new NodeInfoRecyclerViewAdapter(nodeList, currentNode, mListener));
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnNodeListFragmentInteractionListener {
        int OPERATION_NODE_ADD =2;
        int OPERATION_NODE_DELETE =1;
        int OPERATION_NODE_EDIT =0;
        void onNodeSelectedInteraction(NodeInfo nodeInfo, int operation);
    }

}
