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

import com.github.davidbolet.jpascalcoin.common.model.PascPublicKey;

import org.pascalcoin.pascalcoinofficial.R;
import org.pascalcoin.pascalcoinofficial.model.PrivateKeyInfo;

import java.util.List;

public class PrivateKeyListFragment extends Fragment {

    private List<PrivateKeyInfo> privakeKeyList;
    private PascPublicKey currentKey;
    private OnKeyListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private FloatingActionButton addButton;
    private FloatingActionButton importButton;

    public static PrivateKeyListFragment newInstance(List<PrivateKeyInfo> privateKeyInfos, PascPublicKey currentKey) {
        PrivateKeyListFragment fragment = new PrivateKeyListFragment();
        fragment.privakeKeyList =privateKeyInfos;
        fragment.currentKey =currentKey;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyinfo_list, container, false);

        addButton =view.findViewById(R.id.add_key);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener!=null)
                    mListener.onKeySelectedInteraction(null, OnKeyListFragmentInteractionListener.OPERATION_ADD);
            }
        });
        importButton = view.findViewById(R.id.import_key_button);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener!=null)
                    mListener.onKeySelectedInteraction(null, OnKeyListFragmentInteractionListener.OPERATION_IMPORT);
            }
        });
        recyclerView=view.findViewById(R.id.nodeinfo_list);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(new PrivateKeyInfoRecyclerViewAdapter(privakeKeyList, currentKey, mListener));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnKeyListFragmentInteractionListener) {
            mListener = (OnKeyListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnKeyListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setPrivakeKeyList(List<PrivateKeyInfo> privateKeyInfos) {
        this.privakeKeyList =privateKeyInfos;
        if (recyclerView!=null) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
            recyclerView.setAdapter(new PrivateKeyInfoRecyclerViewAdapter(privakeKeyList, currentKey, mListener));
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
    public interface OnKeyListFragmentInteractionListener {
        int OPERATION_IMPORT=3;
        int OPERATION_ADD=2;
        int OPERATION_DELETE=1;
        int OPERATION_EDIT=0;
        void onKeySelectedInteraction(PrivateKeyInfo keyInfo, int operation);
    }

}
