package org.pascalcoin.pascalcoinofficial.controller;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import org.pascalcoin.pascalcoinofficial.R;
import org.pascalcoin.pascalcoinofficial.model.PrivateKeyInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdvancedSearchAccountFragment extends Fragment  {

    private static AdvancedSearchAccountFragment _instance;

    private EditText inputNameSearchText;
    private EditText inputNumSearchText;
    private Button buttonExecuteSearch;
    private RangeSeekBar<Float> seekBar;
    private SearchAccountFragment searchAccountFragment;

    private SearchAccountFragment.OnAccountSearchInteraction mListener;
    private String searchText;
    private Double minBalance;
    private Double maxBalance;
    private Integer startAccount=-1;
    private Boolean showOnlyForSale=false;
    private Boolean exactFilterText=false;
    private Boolean exactFilterNum=false;
    private PrivateKeyInfo keySelected;
    private PrivateKeyInfo lastKeySelected;
    private List<PrivateKeyInfo> userKeys;

    public static AdvancedSearchAccountFragment getInstance(SearchAccountFragment.OnAccountSearchInteraction listener,List<PrivateKeyInfo> keyInfos, SearchAccountFragment searchAccountFragment) {
        if (_instance==null) {
            _instance = new AdvancedSearchAccountFragment();
            _instance.userKeys = keyInfos;
            _instance.mListener = listener;
            _instance.searchAccountFragment=searchAccountFragment;
        }
        return _instance;
    }

    public AdvancedSearchAccountFragment() {

    }

    public void setmListener(SearchAccountFragment.OnAccountSearchInteraction mListener) {
        this.mListener=mListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advanced_search_form, container, false);
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(false);

        inputNameSearchText=view.findViewById(R.id.search_bar_edit_text_name);
        final Switch switchExactName=view.findViewById(R.id.switch_exact_name);
        switchExactName.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                exactFilterText=switchExactName.isChecked();
            }
        });
        inputNumSearchText=view.findViewById(R.id.search_bar_edit_text_number);
        final Switch switchExactNum=view.findViewById(R.id.switch_exact_number);
        switchExactNum.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                exactFilterNum=switchExactNum.isChecked();
            }
        });
        seekBar = view.findViewById(R.id.rangeSeekbar);
        seekBar.setRangeValues(Float.parseFloat("0.00"), 2000000f);
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Float>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Float minValue, Float maxValue) {
                //Now you have the minValue and maxValue of your RangeSeekbar
                //Toast.makeText(getContext(), minValue + "-" + maxValue, Toast.LENGTH_LONG).show();
                minBalance=minValue.doubleValue();
                maxBalance=maxValue.doubleValue();
            }
        });
        seekBar.setNotifyWhileDragging(true);
        final Switch switchOnlyForSale=view.findViewById(R.id.switchOnlyForSale);
        switchOnlyForSale.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                showOnlyForSale=switchOnlyForSale.isChecked();
            }
        });
        final Spinner listUserKeys= view.findViewById(R.id.listUserKeys);
        List<String> userKeyAsString=new ArrayList<>();

        for(PrivateKeyInfo keyInfo:userKeys) {
            userKeyAsString.add(String.format(Locale.getDefault(),"%s (%s)",keyInfo.getName(),keyInfo.getPascKeyType().name()));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, userKeyAsString);
        listUserKeys.setAdapter(adapter);
        listUserKeys.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                keySelected=userKeys.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                keySelected=null;
            }
        });
        listUserKeys.setVisibility(View.GONE);
        final Switch switchShowFromKey = view.findViewById(R.id.switchShowFromKey);
        switchShowFromKey.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (switchShowFromKey.isChecked()) {
                    keySelected=lastKeySelected;
                    listUserKeys.setVisibility(View.VISIBLE);
                } else {
                    lastKeySelected=keySelected;
                    listUserKeys.setVisibility(View.GONE);
                }
            }
        });
        buttonExecuteSearch = view.findViewById(R.id.buttonAdvancedSearch);
        buttonExecuteSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText = inputNameSearchText.getText().toString().trim();
                if (!"".equals(inputNumSearchText.getText().toString())) {
                   try {
                       startAccount = Integer.parseInt(inputNumSearchText.getText().toString());
                   }catch (NumberFormatException nfe) {
                       startAccount=-1;
                   }
                }
                if (startAccount>=0 && exactFilterNum) {
                    mListener.onFireAccountSearch(null,startAccount,true,showOnlyForSale,minBalance,maxBalance,searchAccountFragment);
                }
                else
                {
                    mListener.onFireAccountSearch("".equals(searchText)?null:searchText,startAccount<0?0:startAccount,exactFilterText,showOnlyForSale,minBalance,maxBalance,searchAccountFragment);
                }
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


}
