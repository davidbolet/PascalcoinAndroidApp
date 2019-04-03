package org.pascalcoin.pascalcoinofficial.data;

import android.content.Context;
import android.support.v7.preference.ListPreference;
import android.util.AttributeSet;

import org.pascalcoin.pascalcoinofficial.model.NodeInfo;
import org.pascalcoin.pascalcoinofficial.services.PreferencesService;

import java.util.ArrayList;
import java.util.List;

public class NodeListPreferences extends ListPreference {
    Context context;
    PreferencesService preferencesService;
    List<CharSequence> entries=new ArrayList<>();
    List<CharSequence> values=new ArrayList<>();

    public NodeListPreferences(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        preferencesService=PreferencesService.getInstance(context);
        List<NodeInfo> nodes=preferencesService.getNodeInfos();

        for (NodeInfo node:nodes)
        {
            entries.add(node.getName());
            values.add(node.getName());
        }
        setEntries(entries.toArray(new CharSequence[entries.size()]));
        setEntryValues(values.toArray(new CharSequence[values.size()]));
        setValueIndex(initializeIndex());

    }

    private int initializeIndex() {
            NodeInfo selectedNode=preferencesService.getSelectedNode();
            return values.indexOf(selectedNode.getName())<0?0:values.indexOf(selectedNode.getName());

        }


}
