package org.pascalcoin.pascalcoinofficial.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.pascalcoin.pascalcoinofficial.R;
import com.github.davidbolet.jpascalcoin.api.model.Account;
import com.github.davidbolet.jpascalcoin.api.model.AccountState;

import org.pascalcoin.pascalcoinofficial.AccountActivity;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyAccountsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyAccountsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyAccountsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private TextView intro;
    private TextView textViewMyAccounts;
    private List<Account> accounts;
    private Integer totalAccountsCount=0;
    private RecyclerView recyclerView;
    private MyAccountsRecyclerViewAdapter myAccountsRecyclerViewAdapter;
    private StaggeredGridLayoutManager layoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;
    private TextView textViewPrice;
    private TextView textViewFunds;
    private String txtPascPrice;
    private String txtUserFunds;
    private boolean landscape;

    public MyAccountsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param accounts Parameter 1.
     * @return A new instance of fragment MyAccountsFragment.
     */
    public static MyAccountsFragment newInstance(List<Account> accounts) {
        MyAccountsFragment fragment = new MyAccountsFragment();
        fragment.setAccounts(accounts);
        return fragment;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts=accounts;
        if (recyclerView!=null) {
            ((MyAccountsRecyclerViewAdapter)recyclerView.getAdapter()).setAccounts(accounts);
            recyclerView.getAdapter().notifyDataSetChanged();
        }
        if (this.totalAccountsCount==0) this.totalAccountsCount=accounts.size();
        if (accounts.size()>0 && intro!=null)
            intro.setVisibility(View.GONE);
        this.setTotalAccounts(accounts.size());
    }

    public void setPascPrice(String pascPrice) {
        this.txtPascPrice=pascPrice;
        if (textViewPrice!=null) {
            textViewPrice.setText(pascPrice);
            textViewPrice.refreshDrawableState();
        }
    }

    public void setUserFunds(String userFunds) {
        this.txtUserFunds=userFunds;
        if (textViewFunds!=null) {
            textViewFunds.setText(txtUserFunds);
            textViewFunds.refreshDrawableState();
        }
    }

    @SuppressLint("StringFormatInvalid")
    public void setTotalAccounts(Integer totalAccounts) {
        this.totalAccountsCount=totalAccounts;
        if (!isAdded()) return;
        if (textViewMyAccounts!=null ) {
            textViewMyAccounts.setText(getString(R.string.pasc_account_list, totalAccounts));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        int currentOrientation = getResources().getConfiguration().orientation;
        this.landscape=currentOrientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        int currentOrientation = getResources().getConfiguration().orientation;
        this.landscape=currentOrientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(false);
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_my_accounts, container, false);
        intro = view.findViewById(R.id.textViewResume);
        textViewPrice= view.findViewById(R.id.textViewPrice);
        textViewFunds=view.findViewById(R.id.textViewFunds);
        textViewPrice.setText(txtPascPrice);
        textViewFunds.setText(txtUserFunds);
        textViewMyAccounts = view.findViewById(R.id.textViewMyAccounts);
        textViewMyAccounts.setText(getString(R.string.pasc_account_list,this.totalAccountsCount==0 && this.accounts!=null?this.accounts.size():this.totalAccountsCount));
        recyclerView = view.findViewById(R.id.recycler_view_my_accounts);
        layoutManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        myAccountsRecyclerViewAdapter = new MyAccountsRecyclerViewAdapter(getActivity().getApplicationContext(),accounts,landscape);
        recyclerView.setAdapter(myAccountsRecyclerViewAdapter);
/*        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if (totalAccountsCount<accounts.size()) {
                    ((OnLoadMoreAccountsListener) getContext()).onLoadMoreAccounts(totalItemsCount);
                }

            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);*/


        myAccountsRecyclerViewAdapter.setOnItemClickListener(new MyAccountsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {

                switch (view.getId()) {

                    case R.id.imageButtonSendPasc:
                        ((OnFragmentInteractionListener) getContext()).onFragmentInteraction(accounts.get(position),AccountActivity.OP_SEND);
                        break;
                    case R.id.imageButtonOperations:
                        ((OnFragmentInteractionListener) getContext()).onFragmentInteraction(accounts.get(position),AccountActivity.OP_OPERATIONS);
                        break;
                    case R.id.imageButtonSendAccount:
                        ((OnFragmentInteractionListener) getContext()).onFragmentInteraction(accounts.get(position),AccountActivity.OP_TRANSFER);
                        break;
                    case R.id.imageButtonListAccount:
                        ((OnFragmentInteractionListener) getContext()).onFragmentInteraction(accounts.get(position),AccountActivity.OP_LIST);
                        break;
                    case R.id.imageButtonDeListAccount:
                        ((OnFragmentInteractionListener) getContext()).onFragmentInteraction(accounts.get(position),AccountActivity.OP_DELIST);
                        break;
                    case R.id.btn_show_ops:
                        PopupMenu popup = new PopupMenu(getContext(), view);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch(item.getItemId()) {
                                    case R.id.action_transfer_account:
                                        ((OnFragmentInteractionListener) getContext()).onFragmentInteraction(accounts.get(position),AccountActivity.OP_TRANSFER);
                                        return true;
                                    case R.id.action_change_info:
                                        ((OnFragmentInteractionListener) getContext()).onFragmentInteraction(accounts.get(position),AccountActivity.OP_CHANGE);
                                        return true;
                                    case R.id.action_list_account:
                                        ((OnFragmentInteractionListener) getContext()).onFragmentInteraction(accounts.get(position),AccountActivity.OP_LIST);
                                        return true;
                                    case R.id.action_delist_account:
                                        ((OnFragmentInteractionListener) getContext()).onFragmentInteraction(accounts.get(position),AccountActivity.OP_DELIST);
                                        return true;
                                }
                                return true;
                            }
                        });
                        popup.inflate(R.menu.menu_account_operations);
                        MenuItem list= popup.getMenu().findItem(R.id.action_list_account);
                        MenuItem delist= popup.getMenu().findItem(R.id.action_delist_account);
                        if (accounts.get(position).getState().equals(AccountState.LISTED)) {
                            list.setVisible(false);
                            delist.setVisible(true);
                        }
                        else {
                            list.setVisible(accounts.size()>0);
                            delist.setVisible(false);
                        }
                        popup.show();
                        break;
                    case R.id.imageButtonChangeAccount:
                        ((OnFragmentInteractionListener) getContext()).onFragmentInteraction(accounts.get(position),AccountActivity.OP_CHANGE);
                        break;

                }
            }
        });
        if (accounts==null|| accounts.size()==0) {
            intro.setText(R.string.txt_presentation);
        }
        else
        {
            intro.setVisibility(View.GONE);
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Account account, String operation);
    }

    public interface OnLoadMoreAccountsListener {
        void onLoadMoreAccounts(int numAccountsLoaded);
    }
}
