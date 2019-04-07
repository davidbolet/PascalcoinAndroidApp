package org.pascalcoin.pascalcoinofficial.services;

import android.content.Context;
import android.content.SharedPreferences;

import org.pascalcoin.pascalcoinofficial.data.DatabaseHelper;
import org.pascalcoin.pascalcoinofficial.model.NodeInfo;
import org.pascalcoin.pascalcoinofficial.model.PrivateKeyInfo;

import java.util.List;
import java.util.Locale;

public class PreferencesService {

    public static final String PREFS_NAME = "pascalcoinPrefs";
    public static final String SELECTED_CURRENCY="default_currency";
    public static final String SELECTED_PUBLIC_KEY="selected_pubkey";
    public static final String SELECTED_NODE="selected_node";
    public static final String DECRYPT_PASSWORDS="payload_passwords";
    public static final String APPLICATION_FINGERPRINT="application_fingerprint";
    public static final String FINGERPRINT_KEY="pascalcoinOfficial";
    public static final String NUM_ACCOUNTS="numAccounts";
    public static final Integer NUM_ACCOUNTS_DEFAULT=100;
    public static final String CURRENCY_DEFAULT="USD";

    private static PreferencesService _instance;
    private Context context;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper databaseHelper;
    private Boolean useFingerprint;
    private PrivateKeyInfo privateKeyInfo;
    private NodeInfo nodeInfo;

    public static PreferencesService getInstance(Context context) {
        if (_instance==null)
            _instance=new PreferencesService(context);
        return _instance;
    }

    private PreferencesService(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        databaseHelper = DatabaseHelper.getInstance(context);
        useFingerprint = sharedPreferences.getBoolean(APPLICATION_FINGERPRINT, false);

        String privateKeyInfoName=sharedPreferences.getString(SELECTED_PUBLIC_KEY,DatabaseHelper.DEFAULT_PUBLIC_KEY_NAME);
        privateKeyInfo=databaseHelper.getKeyByName(privateKeyInfoName);

        String defaultNodeString= Locale.getDefault().getLanguage().startsWith("zh")?DatabaseHelper.PASCALCOIN_APP_ASIA_DEFAULT_NODE_NAME:DatabaseHelper.PASCALCOIN_APP_EUROPE_DEFAULT_NODE_NAME;

        String nodeInfoName=sharedPreferences.getString(SELECTED_NODE,defaultNodeString);
        nodeInfo=databaseHelper.getNodeInfoByName(nodeInfoName);
    }

    public List<NodeInfo> getNodeInfos() {
        return databaseHelper.getNodeInfos();
    }

    public List<PrivateKeyInfo> getPrivateKeyInfos() {
        return databaseHelper.getPrivateKeyInfos();
    }

    public void saveSelectedCurrency(String selectedCurrency) {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(SELECTED_CURRENCY,selectedCurrency);
        editor.apply();
    }

    public void saveUseFingerprint(Boolean useFingerprint) {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(APPLICATION_FINGERPRINT,useFingerprint);
        editor.apply();
        this.useFingerprint=useFingerprint;
    }

    public void saveDecryptPasswords(String passwordList) {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(DECRYPT_PASSWORDS,passwordList);
        editor.apply();
    }

    public void saveSelectedPublicKey(String selectedPublicKey) {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(SELECTED_PUBLIC_KEY,selectedPublicKey);
        editor.apply();
        privateKeyInfo=databaseHelper.getKeyByName(selectedPublicKey);
    }

    public void saveSelectedNode(String selectedNodeName) {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(SELECTED_NODE,selectedNodeName);
        editor.apply();
        nodeInfo=databaseHelper.getNodeInfoByName(selectedNodeName);
    }

    public void saveNumAccounts(Integer numAccounts) {
        Integer num=numAccounts==null?NUM_ACCOUNTS_DEFAULT:numAccounts;
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(NUM_ACCOUNTS,num);
        editor.apply();
    }


    public Integer getNumAccountsToRetrieve() {
        return sharedPreferences.getInt(NUM_ACCOUNTS,NUM_ACCOUNTS_DEFAULT);
    }

    public void  deleteNodeInfo(NodeInfo toDelete) {
        databaseHelper.deleteNode(toDelete);
    }

    public void addNodeInfo(NodeInfo newNode) {
        databaseHelper.insertNodes(newNode );
    }

    public void updateNodeInfo(NodeInfo nodeInfo) {
        databaseHelper.updateNodes(nodeInfo );
    }

    public void addPrivateKey(PrivateKeyInfo newKey) {
        databaseHelper.insertKeys(newKey);
    }

    public void deletePrivateKey(PrivateKeyInfo privateKeyInfo) {
        databaseHelper.deleteKey(privateKeyInfo);
    }

    public NodeInfo getSelectedNode() {
/*        String nodeInfoName=sharedPreferences.getString(SELECTED_NODE,DatabaseHelper.PASCALCOIN_APP_DEFAULT_NODE_NAME);
        NodeInfo nodeInfo=databaseHelper.getNodeInfoByName(nodeInfoName);
        if (nodeInfo==null) nodeInfo=databaseHelper.getNodeInfoByName(DatabaseHelper.PASCALCOIN_APP_DEFAULT_NODE_NAME);*/
        return nodeInfo;
    }

    public PrivateKeyInfo getSelectedPrivateKeyInfo() {
/*        String privateKeyInfoName=sharedPreferences.getString(SELECTED_PUBLIC_KEY,DatabaseHelper.DEFAULT_PUBLIC_KEY_NAME);
        PrivateKeyInfo privateKeyInfo=databaseHelper.getKeyByName(privateKeyInfoName);
        if (privateKeyInfo==null) {
            privateKeyInfoName = DatabaseHelper.DEFAULT_PUBLIC_KEY_NAME;
            privateKeyInfo = databaseHelper.getKeyByName(privateKeyInfoName);
        }*/
        return privateKeyInfo;
    }

    public Boolean getUseFingerprint() {
        this.useFingerprint=sharedPreferences.getBoolean(APPLICATION_FINGERPRINT,false);
        return this.useFingerprint;
    }

    public String getSelectedCurrency() {
        return sharedPreferences.getString(SELECTED_CURRENCY,CURRENCY_DEFAULT);
    }

    public String[] getPasswords() {
        String passwordList= sharedPreferences.getString(DECRYPT_PASSWORDS,null);
        if (passwordList==null) return new String[] {};
        return passwordList.split(",");
    }

    public void updatePrivateKey(PrivateKeyInfo keyInfo) {
        databaseHelper.updateKey(keyInfo);
    }



}
