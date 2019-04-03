package org.pascalcoin.pascalcoinofficial.data;

import android.content.Context;
import android.support.v7.preference.ListPreference;
import android.util.AttributeSet;

import org.pascalcoin.pascalcoinofficial.model.PrivateKeyInfo;
import org.pascalcoin.pascalcoinofficial.services.PreferencesService;

import java.util.ArrayList;
import java.util.List;

public class KeysListPreferences extends ListPreference {
    PreferencesService preferencesService;
    List<CharSequence> entries=new ArrayList<>();
    List<CharSequence> values=new ArrayList<>();

        public KeysListPreferences(Context context, AttributeSet attrs) {
            super(context, attrs);

            preferencesService=PreferencesService.getInstance(context);
            List<PrivateKeyInfo> storedKeys=preferencesService.getPrivateKeyInfos();

            for (PrivateKeyInfo keyInfo:storedKeys)
            {
                entries.add(keyInfo.getName());
                values.add(keyInfo.getName());
            }
            setEntries(entries.toArray(new CharSequence[entries.size()]));
            setEntryValues(values.toArray(new CharSequence[values.size()]));
            setValueIndex(initializeIndex());
        }

        private int initializeIndex() {

            String selectedKey=preferencesService.getSelectedPrivateKeyInfo().getName();

            return values.indexOf(selectedKey)<0?0:values.indexOf(selectedKey);
        }

}
