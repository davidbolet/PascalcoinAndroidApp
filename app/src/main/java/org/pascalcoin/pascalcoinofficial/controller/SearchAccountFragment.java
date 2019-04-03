package org.pascalcoin.pascalcoinofficial.controller;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.pascalcoin.pascalcoinofficial.AccountActivity;
import org.pascalcoin.pascalcoinofficial.R;
import com.github.davidbolet.jpascalcoin.api.model.Account;

import java.util.List;
import java.util.Locale;

public class SearchAccountFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    private List<Account> accounts;
    private boolean showSend;
    private EditText searchByNumber;
    private ImageButton search_bar_advanced_icon;
    private ImageButton voice_search_button;
    private OnAccountSearchInteraction mListener;
    private RecyclerViewEmptySupport recyclerView;
    private String searchText;

    public static SearchAccountFragment newInstance(OnAccountSearchInteraction mListener, boolean showSend, List<Account> accounts, String lastSearch) {
        SearchAccountFragment result=new SearchAccountFragment();
        result.setmListener(mListener);
        result.setAccounts(accounts);
        result.setShowSend(showSend);
        result.setSearchText(lastSearch);
        return result;
    }

    public SearchAccountFragment() {
        this.showSend =false;
    }

    public void setShowSend(boolean show) {
        this.showSend =show;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts=accounts;
        if (this.recyclerView!=null) {
            ((SearchAccountRecyclerViewAdapter)this.recyclerView.getAdapter()).setmValues(accounts);
            this.recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    public void setmListener(OnAccountSearchInteraction mListener) {
        this.mListener=mListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_accounts_list, container, false);
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(false);
        final SearchAccountFragment searchAccountFragment=this;

        search_bar_advanced_icon= view.findViewById(R.id.search_bar_advanced_icon);
        search_bar_advanced_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFireAdvancedSearch();
            }
        });
        voice_search_button = view.findViewById(R.id.search_bar_voice_icon);
        voice_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        searchByNumber =view.findViewById(R.id.search_bar_edit_text);
        searchByNumber.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (TextUtils.isDigitsOnly(s)) {
                    if (mListener!=null && s.length()>0)
                        mListener.onFireAccountSearch(null,Integer.parseInt(s.toString()),false,null, null, null, searchAccountFragment);
                } else {
                    if (s.length()>3) {
                        if (mListener!=null) mListener.onFireAccountSearch(s.toString(),null,false,null, null, null,searchAccountFragment);
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

        });
        searchByNumber.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Log.d("TEST","layoutchange");
            }
        });
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setEmptyView(view.findViewById(R.id.list_empty));
        recyclerView.setAdapter(new SearchAccountRecyclerViewAdapter( accounts, 5, showSend, mListener));
        setSearchText(this.searchText);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, AccountActivity.REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return false;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
        if (this.searchByNumber!=null)
            this.searchByNumber.setText(searchText);
    }

    public String getSearchText() {
        return searchText;
    }


    public interface OnAccountSearchInteraction {
        void onAccountSelected(Account account, Boolean sale);
        void onFireAccountSearch(String name, Integer number, Boolean exact,Boolean forSale, Double minBalance, Double maxBalance, SearchAccountFragment searchAccountFragment);
        void onFireAdvancedSearch();

    }
}
