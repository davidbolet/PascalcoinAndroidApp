package org.pascalcoin.pascalcoinofficial;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.davidbolet.jpascalcoin.api.model.Account;
import com.github.davidbolet.jpascalcoin.api.model.AccountState;
import com.github.davidbolet.jpascalcoin.api.model.DecryptedPayload;
import com.github.davidbolet.jpascalcoin.api.model.DecryptedPayloadMethod;
import com.github.davidbolet.jpascalcoin.api.model.Operation;
import com.github.davidbolet.jpascalcoin.common.helper.HexConversionsHelper;
import com.github.davidbolet.jpascalcoin.common.helper.OpenSslAes;
import com.github.davidbolet.jpascalcoin.common.model.PascPublicKey;
import com.github.davidbolet.jpascalcoin.common.model.PayLoadEncryptionMethod;
import com.github.davidbolet.jpascalcoin.crypto.helper.EncryptionUtils;
import com.github.davidbolet.jpascalcoin.crypto.model.BuyAccountOperation;
import com.github.davidbolet.jpascalcoin.crypto.model.ChangeAccountOperation;
import com.github.davidbolet.jpascalcoin.crypto.model.DelistAccountOperation;
import com.github.davidbolet.jpascalcoin.crypto.model.ListAccountOperation;
import com.github.davidbolet.jpascalcoin.crypto.model.PascOperation;
import com.github.davidbolet.jpascalcoin.crypto.model.PascPrivateKey;
import com.github.davidbolet.jpascalcoin.crypto.model.TransferOperation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.pascalcoin.pascalcoinofficial.controller.AboutPascalcoinAppFragment;
import org.pascalcoin.pascalcoinofficial.controller.AboutPascalcoinFragment;
import org.pascalcoin.pascalcoinofficial.controller.PrivateKeyAddFragment;
import org.pascalcoin.pascalcoinofficial.controller.NodeAddFragment;
import org.pascalcoin.pascalcoinofficial.controller.AdvancedSearchAccountFragment;
import org.pascalcoin.pascalcoinofficial.controller.BuyAccountFragment;
import org.pascalcoin.pascalcoinofficial.controller.CancelSellAccountFragment;
import org.pascalcoin.pascalcoinofficial.controller.ChangeAccountFragment;
import org.pascalcoin.pascalcoinofficial.controller.EditPascalcoinKeyFragment;
import org.pascalcoin.pascalcoinofficial.controller.FingerprintControllerFragment;
import org.pascalcoin.pascalcoinofficial.controller.PrivateKeyImportFragment;
import org.pascalcoin.pascalcoinofficial.controller.PrivateKeyListFragment;
import org.pascalcoin.pascalcoinofficial.controller.MyAccountsFragment;
import org.pascalcoin.pascalcoinofficial.controller.NoConnectionFragment;
import org.pascalcoin.pascalcoinofficial.controller.NodeListFragment;
import org.pascalcoin.pascalcoinofficial.controller.OperationDetailFragment;
import org.pascalcoin.pascalcoinofficial.controller.OperationListFragment;
import org.pascalcoin.pascalcoinofficial.controller.SearchAccountFragment;
import org.pascalcoin.pascalcoinofficial.controller.SellAccountFragment;
import org.pascalcoin.pascalcoinofficial.controller.SendPascFragment;
import org.pascalcoin.pascalcoinofficial.controller.ShowPublicKeyFragment;
import org.pascalcoin.pascalcoinofficial.controller.TransferAccountFragment;
import org.pascalcoin.pascalcoinofficial.data.KeysListPreferences;
import org.pascalcoin.pascalcoinofficial.data.NodeListPreferences;
import org.pascalcoin.pascalcoinofficial.event.AccountInfoEvent;
import org.pascalcoin.pascalcoinofficial.event.AuthenticationSuccessfulEvent;
import org.pascalcoin.pascalcoinofficial.event.ErrorEvent;
import org.pascalcoin.pascalcoinofficial.event.KeyDecodedEvent;
import org.pascalcoin.pascalcoinofficial.event.ListAccountsEvent;
import org.pascalcoin.pascalcoinofficial.event.ListOperationsEvent;
import org.pascalcoin.pascalcoinofficial.event.NewKeyEvent;
import org.pascalcoin.pascalcoinofficial.event.NewNodeEvent;
import org.pascalcoin.pascalcoinofficial.event.NoConnectionErrorEvent;
import org.pascalcoin.pascalcoinofficial.event.NodeAddedEvent;
import org.pascalcoin.pascalcoinofficial.event.NodeDeletedEvent;
import org.pascalcoin.pascalcoinofficial.event.OperationExecEvent;
import org.pascalcoin.pascalcoinofficial.event.PascPriceEvent;
import org.pascalcoin.pascalcoinofficial.event.PreferenceChangedEvent;
import org.pascalcoin.pascalcoinofficial.helper.PascalUtils;
import org.pascalcoin.pascalcoinofficial.model.NodeInfo;
import org.pascalcoin.pascalcoinofficial.model.PrivateKeyInfo;
import org.pascalcoin.pascalcoinofficial.services.PascalOperationsService;
import org.pascalcoin.pascalcoinofficial.services.PreferencesService;
import org.pascalcoin.pascalcoinofficial.services.rest.InfoServiceProvider;
import org.pascalcoin.pascalcoinofficial.services.rest.PascalcoinServiceProvider;
import org.pascalcoin.pascalcoinofficial.tasks.ReloadPrice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.pascalcoin.pascalcoinofficial.services.PreferencesService.APPLICATION_FINGERPRINT;
import static org.pascalcoin.pascalcoinofficial.services.PreferencesService.DECRYPT_PASSWORDS;
import static org.pascalcoin.pascalcoinofficial.services.PreferencesService.NUM_ACCOUNTS;
import static org.pascalcoin.pascalcoinofficial.services.PreferencesService.NUM_ACCOUNTS_DEFAULT;
import static org.pascalcoin.pascalcoinofficial.services.PreferencesService.PREFS_NAME;
import static org.pascalcoin.pascalcoinofficial.services.PreferencesService.SELECTED_CURRENCY;
import static org.pascalcoin.pascalcoinofficial.services.PreferencesService.SELECTED_NODE;
import static org.pascalcoin.pascalcoinofficial.services.PreferencesService.SELECTED_PUBLIC_KEY;

public class AccountActivity extends AppCompatActivity implements
        MyAccountsFragment.OnFragmentInteractionListener,
        NodeListFragment.OnNodeListFragmentInteractionListener,
        MyAccountsFragment.OnLoadMoreAccountsListener,
        OperationListFragment.OnListOperationFragmentInteractionListener,
        SendPascFragment.OnFragmentSendPascListener,
        ChangeAccountFragment.OnFragmentChangeAccountListener,
        CancelSellAccountFragment.OnFragmentCancelSellAccountListener,
        SellAccountFragment.OnFragmentSellAccountListener,
        TransferAccountFragment.OnFragmentSendAccountListener,
        SearchAccountFragment.OnAccountSearchInteraction,
        BuyAccountFragment.OnFragmentBuyAccountListener,
        PrivateKeyListFragment.OnKeyListFragmentInteractionListener {

    public static final int REQ_CODE_SPEECH_INPUT =19 ;
    private static final String TAG = AccountActivity.class.getSimpleName();
    public static final int SCAN_CODE = 17;
    public static final int SCAN_PRIVATE_KEY = 18;

    public static final String OP_SEND="sendPASC";
    public static final String OP_SEND_DEST="sendPASCDest";
    public static final String OP_OPERATIONS="operations";
    public static final String OP_TRANSFER="transfer";
    public static final String OP_CHANGE="change";
    public static final String OP_LIST="listForSale";
    public static final String OP_DELIST="delistForSale";

    //Value constant params
    public static final String PARAM_ACCOUNT_FROM="originAccount";
    public static final String PARAM_ACCOUNT_TO="destinationAccount";
    public static final String PARAM_AMOUNT="amount";
    public static final String PARAM_FEE="fee";
    public static final String PARAM_PAYLOAD="payload";
    public static final String PARAM_PAYLOAD_ENCRIPTION="payloadEncription";
    public static final String PARAM_PAYLOAD_PWD="payloadPassword";
    public static final String PARAM_PAYER_ACCOUNT="accountPayer";
    public static final String PARAM_BUYING_ACCOUNT = "accountToBuy";
    public static final String PARAM_TRANSFER_ACCOUNT="accountToTransfer";
    public static final String PARAM_NEW_KEY="newPubKey";
    public static final String PARAM_CHANGING_ACCOUNT ="accountToChange";
    public static final String PARAM_SELLNG_ACCOUNT ="accountToSell";
    public static final String PARAM_NEW_NAME ="newName";
    public static final String PARAM_NEW_TYPE ="newType";
    public static final String PARAM_SELLER_ACCOUNT="accountSeller";
    public static final String PARAM_PRICE="price";
    private static final int NUM_DECIMALS_PASCAL = 4;

    private ReloadPrice reloadPrice;
    private InfoServiceProvider infoServiceProvider;
    private PascalcoinServiceProvider pascalcoinServiceProvider;
    private PascalOperationsService pascalOperationsService;
    private String lastPrice;
    private Double pascPrice=0.0;
    private PascPublicKey selectedPublicKey;
    private NodeInfo selectedNode;
    private MyAccountsFragment myAccountsFragment;
    private NodeListFragment nodeListFragment;
    private PrivateKeyListFragment privateKeyListFragment;
    private GeneralPreferenceFragment generalPreferenceFragment;
    private AboutPascalcoinFragment aboutPascalcoinFragment;
    private AboutPascalcoinAppFragment aboutPascalcoinAppFragment;
    private NodeAddFragment nodeAddFragment;
    private ChangeAccountFragment changeAccountFragment;
    private TransferAccountFragment sendAccountFragment;
    private SellAccountFragment sellAccountFragment;
    private CancelSellAccountFragment cancelSellAccountFragment;
    private SendPascFragment sendPasc;
    private SearchAccountFragment searchAccountFragment;
    private SearchAccountFragment searchAccountFragmentResult;
    private BuyAccountFragment buyAccountFragment;
    private NoConnectionFragment noConnectionFragment;
    private AdvancedSearchAccountFragment advancedSearchAccountFragment;
    private PrivateKeyImportFragment privateKeyImportFragment;
    private BottomNavigationView navigationView;

    private String selectedCurrency="EUR";
    private AtomicInteger numAccountsToRetrive=new AtomicInteger(100);
    private Integer navigationSelected=0;
    private PreferencesService preferencesService;
    private List<Account> userAccounts;
    private List<Account> lastResult=new ArrayList<>();
    private String[] decryptionPasswords;
    private String lastSearch;
    private Integer numRetries;
    private String userFunds;
    private boolean canUseFingerprint;
    private boolean lockedFingerprint=false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    navigationSelected=0;
                    break;
                case R.id.navigation_dashboard:
                    navigationSelected=1;
                    break;
                case R.id.navigation_notifications:
                    navigationSelected=3;
                    break;
            }
            callNavigationSelected(navigationSelected);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
        this.userAccounts=new ArrayList<>();
        numRetries=0;
        setContentView(R.layout.activity_account);

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigationView.setVisibility(View.GONE);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        //getSupportActionBar().setTitle(R.string.title_home);
        getSupportActionBar().setIcon(R.drawable.ic_current_logo_clean);

        canUseFingerprint = this.canUseFingerprint();
        if (savedInstanceState!=null && savedInstanceState.containsKey("navigationSelected")) {
            this.navigationSelected = savedInstanceState.getInt("navigationSelected", 0);
            this.lastSearch=savedInstanceState.getString("searchText");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        reloadPrice.cancel();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment= manager.findFragmentById(R.id.rootContainer);
        if (fragment!=null) {
            manager.beginTransaction().remove(fragment).commitAllowingStateLoss();;
        }
        Fragment frag = manager.findFragmentByTag("fragment_show_change_pwd");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commitAllowingStateLoss();;
        }
        frag = manager.findFragmentByTag("fragment_edit_user");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commitAllowingStateLoss();;
        }
        frag = manager.findFragmentByTag("fragment_show_op_detail");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commitAllowingStateLoss();;
        }
        frag = manager.findFragmentByTag("fragment_show_op_detail");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commitAllowingStateLoss();;
        }
        frag = manager.findFragmentByTag("fragment_change_account");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commitAllowingStateLoss();;
        }
        frag = manager.findFragmentByTag("fragment_sell_account");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commitAllowingStateLoss();;
        }
        frag = manager.findFragmentByTag("fragment_cancel_sell_account");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commitAllowingStateLoss();;
        }
        frag = manager.findFragmentByTag("fragment_buy_account");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commitAllowingStateLoss();;
        }
        if (outState!=null) {
            if (navigationSelected != null) {
                outState.putInt("navigationSelected", navigationSelected);
                if (navigationSelected==1 && searchAccountFragment!=null) {
                    outState.putString("searchText", searchAccountFragment.getSearchText());
                }
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadPrice = new ReloadPrice(this, 600,  selectedCurrency);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (preferencesService==null)
            preferencesService = PreferencesService.getInstance(this.getApplicationContext());
        if (infoServiceProvider==null)
            infoServiceProvider= InfoServiceProvider.getInstance();

        selectedCurrency=preferencesService.getSelectedCurrency();
        decryptionPasswords=preferencesService.getPasswords();
        infoServiceProvider.getPrice("account",preferencesService.getSelectedCurrency() );
        selectedPublicKey=preferencesService.getSelectedPrivateKeyInfo().getPascPublicKey();
        selectedNode=preferencesService.getSelectedNode();
        boolean useFingerprint=canUseFingerprint && preferencesService.getUseFingerprint();
        if (useFingerprint)
        {
            loadFingerprintFragment();
        }
        else {
            EventBus.getDefault().register(this);
            pascalcoinServiceProvider = PascalcoinServiceProvider.getInstance(selectedNode.getUrl());
            pascalOperationsService= PascalOperationsService.getInstance(pascalcoinServiceProvider);
            callNavigationSelected(navigationSelected);
            navigationView.setVisibility(View.VISIBLE);
        }
    }

    private void callNavigationSelected(Integer navigationSelected) {
        switch (navigationSelected) {
            case 0:
                if (selectedPublicKey==null) {
                    this.selectedPublicKey = preferencesService.getSelectedPrivateKeyInfo().getPascPublicKey();
                }
                pascalcoinServiceProvider.getPublicKeyAccounts(selectedPublicKey.getEncPubKey(), 0, numAccountsToRetrive.get());
                break;
            case 1:
                loadSearchAccountsFragment();
                break;
            case 3:
                loadPreferencesFragment();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment= manager.findFragmentById(R.id.rootContainer);
        if (fragment instanceof OperationListFragment) {
            if (navigationSelected==0)
                loadMyAccountsFragment(userAccounts);
            else if (navigationSelected==1) {

            }
        }
        else
        {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.logout_menu))
                    .setMessage(getString(R.string.logout_confirm))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            signOut();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }

    private void signOut() {
    }

    /********** UPPER MENU ***********/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!lockedFingerprint)
            getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_nodes_management:
                loadNodesFragment();
                break;
            case R.id.action_keys_management:
                loadKeysFragment();
                break;
            case R.id.action_show_key:
                createShowPKPopupDialog();
                break;
            case R.id.action_about_pasc:
                createShowAboutDialog();
                break;
            case R.id.action_about_app:
                createShowAboutAppDialog();
                break;
            case R.id.action_logout:
                signOut();
                break;
            case android.R.id.home:
                if (navigationSelected==0)
                    loadMyAccountsFragment(this.userAccounts);
                else if (navigationSelected==1)
                    loadSearchAccountsFragment();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void loadNodesFragment() {
        List<NodeInfo> nodeInfos = preferencesService.getNodeInfos();
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment= manager.findFragmentById(R.id.rootContainer);
        if (nodeListFragment==null)
            nodeListFragment = NodeListFragment.newInstance(nodeInfos,selectedNode);

        if (fragment!=null && !(fragment instanceof NodeListFragment)) {
            manager.beginTransaction()
                    .remove(fragment)
                    .add(R.id.rootContainer, nodeListFragment)
                    .commit();
        }
        else if (fragment==null) {
            manager.beginTransaction()
                    .add(R.id.rootContainer, nodeListFragment)
                    .commit();
        }
        nodeListFragment.setNodeList(nodeInfos);
    }

    private void loadKeysFragment() {
        List<PrivateKeyInfo> privateKeyInfos = preferencesService.getPrivateKeyInfos();
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment= manager.findFragmentById(R.id.rootContainer);
        if (privateKeyListFragment ==null)
            privateKeyListFragment = PrivateKeyListFragment.newInstance(privateKeyInfos,selectedPublicKey);

        if (fragment!=null && !(fragment instanceof PrivateKeyListFragment)) {
            manager.beginTransaction()
                    .remove(fragment)
                    .add(R.id.rootContainer, privateKeyListFragment)
                    .commit();
        }
        else if (fragment==null) {
            manager.beginTransaction()
                    .add(R.id.rootContainer, privateKeyListFragment)
                    .commit();
        }
        privateKeyListFragment.setPrivakeKeyList(privateKeyInfos);
    }

    private void createShowAboutDialog() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag("fragment_show_about");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        if (aboutPascalcoinFragment==null)
            aboutPascalcoinFragment = AboutPascalcoinFragment.newInstance();
        aboutPascalcoinFragment.show(manager, "fragment_show_about");
    }

    private void createShowAboutAppDialog() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag("fragment_show_about_app");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        if (aboutPascalcoinAppFragment==null)
            aboutPascalcoinAppFragment = AboutPascalcoinAppFragment.newInstance();
        aboutPascalcoinAppFragment.show(manager, "fragment_show_about_app");
    }

    public void createShowPKPopupDialog() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag("fragment_show_pk");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        ShowPublicKeyFragment pkFragment = ShowPublicKeyFragment.newInstance(selectedPublicKey.getBase58PubKey());
        pkFragment.show(manager, "fragment_show_pk");

    }

    /**** END MENU *******/
    protected synchronized void loadMyAccountsFragment(List<Account> accountsToShow) {

        if (myAccountsFragment==null)
            myAccountsFragment = MyAccountsFragment.newInstance(accountsToShow);

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment= manager.findFragmentById(R.id.rootContainer);

        if (fragment!=null && !(fragment instanceof MyAccountsFragment)) {
            manager.beginTransaction()
                    .remove(fragment)
                    .add(R.id.rootContainer, myAccountsFragment)
                    .commit();
        }
        else if (fragment==null) {
            manager.beginTransaction()
                    .add(R.id.rootContainer, myAccountsFragment)
                    .commit();
        }
        if (lastPrice!=null)
            myAccountsFragment.setPascPrice(lastPrice);
        myAccountsFragment.setAccounts(accountsToShow);

    }

    protected void loadSearchAccountsFragment() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment= manager.findFragmentById(R.id.rootContainer);
        if (searchAccountFragment==null)
            searchAccountFragment=SearchAccountFragment.newInstance(this, userAccounts!=null && userAccounts.size()>0, lastResult, lastSearch);
        searchAccountFragment.setmListener(this);

        if (fragment!=null && !(fragment instanceof SearchAccountFragment)) {
            manager.beginTransaction()
                    .remove(fragment)
                    .add(R.id.rootContainer, searchAccountFragment)
                    .commit();
        }
        if (fragment == null) {
            manager.beginTransaction()
                    .add(R.id.rootContainer, searchAccountFragment)
                    .commit();
        }
        if (fragment==null || fragment instanceof SearchAccountFragment || fragment instanceof AdvancedSearchAccountFragment) {
            searchAccountFragment.setAccounts(lastResult);
        }
    }

    protected void loadPreferencesFragment() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment= manager.findFragmentById(R.id.rootContainer);
        if (generalPreferenceFragment==null)
            generalPreferenceFragment =  GeneralPreferenceFragment.newInstance(canUseFingerprint());

        if (fragment!=null && !(fragment instanceof GeneralPreferenceFragment)) {
            manager.beginTransaction()
                    .remove(fragment)
                    .add(R.id.rootContainer, generalPreferenceFragment)
                    .commit();
        }
        else if (fragment==null) {
            manager.beginTransaction()
                    .add(R.id.rootContainer, generalPreferenceFragment)
                    .commit();
        }
    }

    protected void loadNoConnectionFragment() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment= manager.findFragmentById(R.id.rootContainer);
        if (noConnectionFragment==null)
            noConnectionFragment = NoConnectionFragment.newInstance(pascalcoinServiceProvider,selectedPublicKey.getEncPubKey(),0,preferencesService.getNumAccountsToRetrieve());
        noConnectionFragment.setPascalcoinServiceProvider(pascalcoinServiceProvider);
        noConnectionFragment.setSelectedPublicKey(selectedPublicKey.getEncPubKey());
        noConnectionFragment.setNumAccounts(preferencesService.getNumAccountsToRetrieve());
        if (fragment!=null && !(fragment instanceof NoConnectionFragment)) {
            manager.beginTransaction()
                    .remove(fragment)
                    .add(R.id.rootContainer, noConnectionFragment)
                    .commit();
        }
        else if (fragment==null) {
            manager.beginTransaction()
                    .add(R.id.rootContainer, noConnectionFragment)
                    .commit();
        }
    }

    protected void loadAdvancedSearchFragment() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment= manager.findFragmentById(R.id.rootContainer);
        if (advancedSearchAccountFragment==null)
            advancedSearchAccountFragment =  AdvancedSearchAccountFragment.getInstance(this,preferencesService.getPrivateKeyInfos(),searchAccountFragment);

        if (fragment!=null && !(fragment instanceof AdvancedSearchAccountFragment)) {
            manager.beginTransaction()
                    .remove(fragment)
                    .add(R.id.rootContainer, advancedSearchAccountFragment)
                    .commit();
        }
        else if (fragment==null) {
            manager.beginTransaction()
                    .add(R.id.rootContainer, advancedSearchAccountFragment)
                    .commit();
        }
    }

    protected void loadFingerprintFragment() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment= manager.findFragmentById(R.id.rootContainer);
        FingerprintControllerFragment fingerprintControllerFragment=new FingerprintControllerFragment();
        if (fragment!=null && !(fragment instanceof FingerprintControllerFragment)) {
            manager.beginTransaction()
                    .remove(fragment)
                    .add(R.id.rootContainer, fingerprintControllerFragment)
                    .commit();
        } else {
            manager.beginTransaction()
                    .add(R.id.rootContainer, fingerprintControllerFragment)
                    .commit();
        }
        navigationView.setVisibility(View.GONE);
        lockedFingerprint=true;
        invalidateOptionsMenu();
    }

    private Double getTotalUserFunds() {
        BigDecimal balance=BigDecimal.ZERO;
        for (Account account: userAccounts) {
            balance=balance.add(new BigDecimal(account.getBalance()));
        }
        return balance.setScale(NUM_DECIMALS_PASCAL, RoundingMode.HALF_UP).doubleValue();
    }

    @SuppressLint("StringFormatMatches")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPriceEvent(PascPriceEvent event) {
        lastPrice=String.format("PASC: %.2f %s",event.getPrice(), event.getCurrency());
        this.pascPrice=event.getPrice().doubleValue();
        this.userFunds=String.format(getString(R.string.txtUserFunds, getTotalUserFunds(),getTotalUserFunds()*pascPrice,selectedCurrency));
        if (myAccountsFragment!=null )
        {
            myAccountsFragment.setPascPrice(lastPrice);
            myAccountsFragment.setUserFunds(userFunds);
        }
    }


    @SuppressLint("StringFormatMatches")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPreferenceChangedEvent(PreferenceChangedEvent event) {
        if(event.getUpdatedPreference().getKey().equals(SELECTED_CURRENCY)) {
            preferencesService.saveSelectedCurrency(selectedCurrency);
            selectedCurrency=event.getValue();
            reloadPrice.cancel();
            reloadPrice = new ReloadPrice(this, 600,  selectedCurrency);
        }
        if (event.getUpdatedPreference().getKey().equals(APPLICATION_FINGERPRINT)) {
            preferencesService.saveUseFingerprint(Boolean.parseBoolean(event.getValue()));
        }
        if (event.getUpdatedPreference().getKey().equals(SELECTED_PUBLIC_KEY)) {
            preferencesService.saveSelectedPublicKey(event.getValue());
            this.selectedPublicKey=preferencesService.getSelectedPrivateKeyInfo().getPascPublicKey();
            callNavigationSelected(navigationSelected);
        }
        if (event.getUpdatedPreference().getKey().equals(SELECTED_NODE)) {
            preferencesService.saveSelectedNode(event.getValue());
            this.selectedNode=preferencesService.getSelectedNode();
            pascalcoinServiceProvider = PascalcoinServiceProvider.getInstance(selectedNode.getUrl());
            pascalOperationsService=PascalOperationsService.getInstance(pascalcoinServiceProvider);
            callNavigationSelected(navigationSelected);
        }
        if (event.getUpdatedPreference().getKey().equals(DECRYPT_PASSWORDS)) {
            preferencesService.saveDecryptPasswords(event.getValue());
        }
        if (event.getUpdatedPreference().getKey().equals(NUM_ACCOUNTS)) {
            Integer result=PreferencesService.NUM_ACCOUNTS_DEFAULT;
            try {
                result=Integer.parseInt(event.getValue());
            } catch(NumberFormatException ex) {

            }
            preferencesService.saveNumAccounts(result);
        }
    }

    @SuppressLint("StringFormatMatches")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ListAccountsEvent event) {
        numRetries=0;
        if (event.getPublicKey()!=null && selectedPublicKey.getEncPubKey().equals(event.getPublicKey())) {
            this.userAccounts = event.getListAccounts();
        }
        if (navigationSelected==0) {
            loadMyAccountsFragment(this.userAccounts);
            this.userFunds=String.format(getString(R.string.txtUserFunds, getTotalUserFunds(),getTotalUserFunds()*pascPrice,selectedCurrency));
            if (myAccountsFragment!=null )
            {
                myAccountsFragment.setPascPrice(lastPrice);
                myAccountsFragment.setUserFunds(userFunds);
            }
        }
        else {
            if (searchAccountFragmentResult!=null) {
                if (searchAccountFragmentResult == searchAccountFragment) {
                    lastResult = event.getListAccounts();
                    loadSearchAccountsFragment();
                } else {
                    searchAccountFragmentResult.setAccounts(event.getListAccounts());
                }
            } else {
                lastResult = event.getListAccounts();
                loadSearchAccountsFragment();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ListOperationsEvent event) {
        numRetries=0;
        OperationListFragment operationListFragment=OperationListFragment.newInstance(2, event.getListOperations());
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment= manager.findFragmentById(R.id.rootContainer);
        manager.beginTransaction()
                .remove(fragment)
                .add(R.id.rootContainer, operationListFragment)
                .commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAuthenticationSuccessfulEvent(AuthenticationSuccessfulEvent event) {
        navigationView.setVisibility(View.VISIBLE);
        lockedFingerprint=false;
        invalidateOptionsMenu();
        pascalcoinServiceProvider = PascalcoinServiceProvider.getInstance(selectedNode.getUrl());
        pascalOperationsService= PascalOperationsService.getInstance(pascalcoinServiceProvider);
        callNavigationSelected( navigationSelected);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AccountInfoEvent event) {
        numRetries=0;
        FragmentManager manager = getSupportFragmentManager();

        Fragment frag = manager.findFragmentByTag("fragment_send_funds");
        Fragment fragBuy = manager.findFragmentByTag("fragment_buy_account");
        if (frag != null && frag.isVisible()) {
            ((SendPascFragment) frag).onAccountSelected(event.getAccount(), false);
        } else if (fragBuy!=null) {
            ((BuyAccountFragment) fragBuy).setAccountSeller(event.getAccount());
        } else if (searchAccountFragmentResult!=null) {
            if (searchAccountFragmentResult == searchAccountFragment) {
                lastResult = Collections.singletonList(event.getAccount());
                loadSearchAccountsFragment();
            } else {
                searchAccountFragmentResult.setAccounts(Collections.singletonList(event.getAccount()));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OperationExecEvent event) {
        numRetries=0;
        Toast.makeText(this.getApplicationContext(), getText(R.string.operation_deployed), Toast.LENGTH_SHORT).show();
        if (event.getListOperations()!=null && event.getListOperations().size()>0)
            onOperationSelectedInteraction(event.getListOperations().get(0));
        else
            pascalcoinServiceProvider.getPublicKeyAccounts(selectedPublicKey.getEncPubKey(), 0, numAccountsToRetrive.get());

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent event) {

        //Authentication error
        if (event.getErrorCode()==401) {
            Toast.makeText(this.getApplicationContext(), getText(R.string.error_server_security), Toast.LENGTH_SHORT).show();
            signOut();
        }
        else
        {
            if (event.getErrorCode()==424) {
                Toast.makeText(this.getApplicationContext(), getText(R.string.error_max_zero_fee_operations), Toast.LENGTH_SHORT).show();
            }
            else {
                //Other error
                Toast.makeText(this.getApplicationContext(), event.getErrorMsg(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoConnectionErrorEvent(NoConnectionErrorEvent event) {
        numRetries++;
        if (numRetries>3) {
           loadNoConnectionFragment();
        }
        else {
            pascalcoinServiceProvider.getPublicKeyAccounts(selectedPublicKey.getEncPubKey(),0, preferencesService.getNumAccountsToRetrieve());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNodeAddedEvent(NodeAddedEvent event) {
        List<NodeInfo>  nodes=preferencesService.getNodeInfos();
        if (nodeListFragment!=null)
            nodeListFragment.setNodeList(preferencesService.getNodeInfos());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNodeDeletedEvent(NodeDeletedEvent event) {
        Toast.makeText(this,getString(R.string.node_deleted, event.getDeletedNodeName()), Toast.LENGTH_LONG);
        List<NodeInfo>  nodes=preferencesService.getNodeInfos();
        if (nodeListFragment!=null)
            nodeListFragment.setNodeList(nodes);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewNodeEvent(NewNodeEvent event) {
        loadFormNewNode();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewKeyEvent(NewKeyEvent event) {
        loadFormNewKey(null, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onKeyDecodedEvent(KeyDecodedEvent event) {
        loadFormNewKey(event.getKeyInfo(), event.getPassword());
    }

    @Override
    public void onFragmentInteraction(Account account, String operation) {

       if (operation.equals(OP_OPERATIONS)) {
            pascalcoinServiceProvider.getAccountOperations(account.getAccount(), null, null);
        }
       if (operation.equals(OP_SEND)) {
            FragmentManager manager = getSupportFragmentManager();
            if (sendPasc==null)
                sendPasc= SendPascFragment.newInstance(account, userAccounts, pascalcoinServiceProvider);
            sendPasc.setUserAccounts(userAccounts);
            sendPasc.setAccountFrom(account);
            sendPasc.show(manager, "fragment_send_funds");
        }
        if (operation.equals(OP_SEND_DEST)) {
            FragmentManager manager = getSupportFragmentManager();
            if (sendPasc==null)
                sendPasc= SendPascFragment.newInstance(userAccounts.get(0), userAccounts, pascalcoinServiceProvider);
            sendPasc.setAccountDest(account);
            sendPasc.setUserAccounts(userAccounts);
            sendPasc.show(manager, "fragment_send_funds");
        }
       if (operation.equals(OP_TRANSFER)) {
            FragmentManager manager = getSupportFragmentManager();
            if (sendAccountFragment==null)
                sendAccountFragment = TransferAccountFragment.newInstance(account,userAccounts);
            sendAccountFragment.setAccountToTransfer(account);
            sendAccountFragment.setUserAccounts(userAccounts);
            sendAccountFragment.show(manager, "fragment_send_account");
        }
        if (operation.equals(OP_CHANGE)) {
            FragmentManager manager = getSupportFragmentManager();
            if (changeAccountFragment==null)
                changeAccountFragment = ChangeAccountFragment.newInstance(account,userAccounts);
            changeAccountFragment.setUserAccounts(userAccounts);
            changeAccountFragment.setAccountToChange(account);
            changeAccountFragment.show(manager, "fragment_change_account");
        }
        if (operation.equals(OP_LIST)) {
            FragmentManager manager = getSupportFragmentManager();
            if (sellAccountFragment==null)
                sellAccountFragment = SellAccountFragment.newInstance(account,userAccounts);
            sellAccountFragment.setUserAccounts(userAccounts);
            sellAccountFragment.show(manager, "fragment_sell_account");
        }
        if (operation.equals(OP_DELIST)) {
            FragmentManager manager = getSupportFragmentManager();
            if (cancelSellAccountFragment==null)
                cancelSellAccountFragment = CancelSellAccountFragment.newInstance(account,userAccounts);
            cancelSellAccountFragment.setUserAccounts(userAccounts);
            cancelSellAccountFragment.show(manager, "fragment_cancel_sell_account");
        }
    }

    @Override
    public void onLoadMoreAccounts(int numAccountsLoaded) {

    }


    public void createOpDetailPopupDialog(Operation operation, DecryptedPayload decryptedPayload) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag("fragment_show_op_detail");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        OperationDetailFragment opFragment = OperationDetailFragment.newInstance(operation, decryptedPayload);
        opFragment.show(manager, "fragment_show_op_detail");

    }

    private DecryptedPayload tryDecrypt(PrivateKeyInfo privateKeyInfo, String[] decryptionPasswords,Operation operation) {
        DecryptedPayload dp=new DecryptedPayload();
        dp.setOriginalPayload(operation.getPayLoad());
        if (decryptionPasswords != null && decryptionPasswords.length>0) {
            String res = null;
            String pwd = "";
            for (int i = 0; i < decryptionPasswords.length && res == null; i++) {
                pwd = decryptionPasswords[i];
                try {
                    res = OpenSslAes.decrypt(pwd, operation.getPayLoad());
                } catch (Exception ex) {
                }
            }
            if (res != null) {
                dp.setDecryptPassword(pwd);
                dp.setEncodedPubKey(operation.getEncPubKey());
                dp.setPayloadMethod(DecryptedPayloadMethod.PWD);
                dp.setUnencryptedPayload(res);
                return dp;
            }
        }
        if (!privateKeyInfo.isEncrypted()) {
            PascPrivateKey pk=PascPrivateKey.fromPrivateKey(preferencesService.getSelectedPrivateKeyInfo().getPrivateKey(),preferencesService.getSelectedPrivateKeyInfo().getPascKeyType());
            try {
                String result = new String(EncryptionUtils.doPascalcoinEciesDecrypt(pk, operation.getPayLoad()));
                dp.setPayloadMethod(DecryptedPayloadMethod.KEY);
                dp.setUnencryptedPayload(result);
                dp.setResult(true);
                return dp;
            } catch(Exception ex) {
            }
        } else {
            Log.d(TAG,"Private key is encrypted, can't use key to unencrypt payload");
        }
        try {
            String res = new String(HexConversionsHelper.decodeStr2Hex(operation.getPayLoad()));
            if (res.startsWith("Salted_") || !PascalUtils.isValidText(res)) {
                dp.setResult(false); //No se pudo desencriptar
            } else {
                dp.setResult(true);
                dp.setUnencryptedPayloadHex(operation.getPayLoad());
                dp.setUnencryptedPayload(res);
            }
        } catch(Exception ex) {
            dp.setResult(false);
        }
        return  dp;
    }

    @Override
    public void onOperationSelectedInteraction(Operation operation) {
        if (operation.getPayLoad()==null || operation.getPayLoad().length()<2 || navigationSelected>0)
            createOpDetailPopupDialog(operation,null);
        else {
            DecryptedPayload dp=tryDecrypt(preferencesService.getSelectedPrivateKeyInfo(),  decryptionPasswords,operation);
            createOpDetailPopupDialog(operation, dp);
        }
    }

    @Override
    public void onFragmentInteraction(Bundle params, final Account accountFrom, final Account accountTo) {
        final Double amount = params.getDouble(PARAM_AMOUNT);
        final Double fee = params.getDouble(PARAM_FEE,0.0001);
        final String payload= params.getString(PARAM_PAYLOAD, null);
        final String payloadEncryption=params.getString(PARAM_PAYLOAD_ENCRIPTION,"NONE");
        final String payloadPwd=params.getString(PARAM_PAYLOAD_PWD, null);
        final PrivateKeyInfo keyInfo= preferencesService.getSelectedPrivateKeyInfo();
        new android.support.v7.app.AlertDialog.Builder(this).setTitle(getText(R.string.txt_send)).setMessage(getString(R.string.send_pasc_confirm, amount, accountFrom.getAccount(), PascalUtils.calculateChecksum(accountFrom.getAccount()), accountTo.getAccount(), PascalUtils.calculateChecksum(accountTo.getAccount()))).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {

                TransferOperation operation=new TransferOperation(
                        accountFrom.getAccount(),
                        accountTo.getAccount(),
                        keyInfo.getPascPublicKey(),
                        PascPublicKey.fromEncodedPubKey(accountTo.getEncPubkey()),
                        accountFrom.getnOperation()+1,
                        amount,
                        fee,
                        payload!=null?payload.getBytes():"".getBytes(),
                        PayLoadEncryptionMethod.valueOf(payloadEncryption),
                        payloadPwd);
                if (keyInfo.isEncrypted()) {
                    showDialogPasswordPrompt( keyInfo, operation);
                }
                else {
                    pascalOperationsService.signAndExecute(operation, keyInfo.getPrivateKey(),keyInfo);
                }
            }
        }).setNegativeButton(android.R.string.no, null).show();
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onChangeAccount(Bundle params, final Account accountFrom) {
        final Integer accountPayer = params.getInt(PARAM_PAYER_ACCOUNT);
        final Integer accountToChange =params.getInt(PARAM_CHANGING_ACCOUNT);
        final String newName = params.getString(PARAM_NEW_NAME);
        final Integer newType = params.getInt(PARAM_NEW_TYPE);
        final Double fee = params.getDouble(PARAM_FEE,0.0001);
        final String payload= params.getString(PARAM_PAYLOAD, null);
        final String payloadEncription=params.getString(PARAM_PAYLOAD_ENCRIPTION, "NONE");
        final String payloadPwd=params.getString(PARAM_PAYLOAD_PWD, null);
        final PrivateKeyInfo keyInfo= preferencesService.getSelectedPrivateKeyInfo();

        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle(getText(R.string.txt_change_account))
                .setMessage(getString(R.string.change_acc_confirm, accountToChange, PascalUtils.calculateChecksum(accountToChange), newName, newType))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ChangeAccountOperation operation=new ChangeAccountOperation(accountFrom.getAccount(),
                                keyInfo.getPascPublicKey(),
                                accountPayer,
                                accountFrom.getnOperation()+1,
                                fee,
                                newName,
                                null,
                                newType,
                                payload!=null?payload.getBytes():"".getBytes(),
                                PayLoadEncryptionMethod.valueOf(payloadEncription),
                                payloadPwd
                        );
                        if (keyInfo.isEncrypted()) {
                            showDialogPasswordPrompt( keyInfo, operation);
                        }
                        else {
                            pascalOperationsService.signAndExecute(operation, keyInfo.getPrivateKey(),keyInfo);
                        }
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onSendAccount(Bundle params, final Account accountPayer, final Account accountToTransfer) {
        final String newPubKey = params.getString(PARAM_NEW_KEY);
        final Double fee = params.getDouble(PARAM_FEE,0.0001);
        final String payload= params.getString(PARAM_PAYLOAD, null);
        final String payloadEncription=params.getString(PARAM_PAYLOAD_ENCRIPTION, "NONE");
        final String payloadPwd=params.getString(PARAM_PAYLOAD_PWD, null);
        final PrivateKeyInfo keyInfo= preferencesService.getSelectedPrivateKeyInfo();

        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle(getText(R.string.txt_send_account))
                .setMessage(getString(R.string.send_acc_confirm, accountToTransfer.getAccount(), PascalUtils.calculateChecksum(accountToTransfer.getAccount()), newPubKey))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        ChangeAccountOperation operation=new ChangeAccountOperation(accountToTransfer.getAccount(),
                                keyInfo.getPascPublicKey(),
                                accountPayer.getAccount(),
                                accountPayer.getnOperation()+1,
                                fee,
                                null,
                                PascPublicKey.fromEncodedPubKey(newPubKey),
                                null,
                                payload!=null?payload.getBytes():"".getBytes(),
                                PayLoadEncryptionMethod.valueOf(payloadEncription),
                                payloadPwd
                        );
                        if (keyInfo.isEncrypted()) {
                            showDialogPasswordPrompt( keyInfo, operation);
                        }
                        else {
                            pascalOperationsService.signAndExecute(operation, keyInfo.getPrivateKey(),keyInfo);
                        }
                        }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onSellAccount(Bundle params, final Account accountToSell, final Account accountPayer, final Account accountSeller) {

        final Double price = params.getDouble(PARAM_PRICE);
        final Double fee = params.getDouble(PARAM_FEE,0.0001);
        final String payload= params.getString(PARAM_PAYLOAD, null);
        final String payloadEncription=params.getString(PARAM_PAYLOAD_ENCRIPTION, "NONE");
        final String payloadPwd=params.getString(PARAM_PAYLOAD_PWD, null);
        final PrivateKeyInfo keyInfo= preferencesService.getSelectedPrivateKeyInfo();

        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.list_account_sale,accountToSell.getAccount()))
                .setMessage(getString(R.string.sell_acc_confirm, accountToSell.getAccount(), PascalUtils.calculateChecksum(accountToSell.getAccount()), price))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ListAccountOperation operation = new ListAccountOperation( accountPayer.getAccount(), keyInfo.getPascPublicKey(),accountToSell.getAccount(), accountSeller.getAccount(),null, accountPayer.getnOperation()+1, price,fee,0, payload!=null?payload.getBytes():"".getBytes(), PayLoadEncryptionMethod.valueOf(payloadEncription), payloadPwd);
                        if (keyInfo.isEncrypted()) {
                            showDialogPasswordPrompt( keyInfo, operation);
                        }
                        else {
                            pascalOperationsService.signAndExecute(operation, keyInfo.getPrivateKey(),keyInfo);
                        }
                        /*PascPrivateKey privateKey=PascPrivateKey.fromPrivateKey(keyInfo.getPrivateKey(),keyInfo.getPascKeyType());
                        byte[] opDigest=operation.generateOpDigest(4.0f);
                        OfflineSignResult res=privateKey.sign(opDigest);
                        String rawOps=operation.getRawOperations(res.getStringR(), res.getStringS());
                        pascalcoinServiceProvider.executeOperations(rawOps,operation);*/
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onCancelSellAccount(Bundle params, final Account accountPayer, final Account accountToSell) {

        final Double fee = params.getDouble(PARAM_FEE,0.0001);
        final String payload= params.getString(PARAM_PAYLOAD, null);
        final String payloadEncription=params.getString(PARAM_PAYLOAD_ENCRIPTION, "NONE");
        final String payloadPwd=params.getString(PARAM_PAYLOAD_PWD, null);
        final PrivateKeyInfo keyInfo= preferencesService.getSelectedPrivateKeyInfo();

        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.delist_account_sale,accountToSell.getAccount()))
                .setMessage(getString(R.string.cancel_sell_acc_confirm, accountToSell.getAccount(), PascalUtils.calculateChecksum(accountToSell.getAccount())))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DelistAccountOperation operation = new DelistAccountOperation( accountToSell.getAccount(), accountPayer.getAccount(),  keyInfo.getPascPublicKey(), accountPayer.getnOperation()+1, fee,payload!=null?payload.getBytes():"".getBytes(), PayLoadEncryptionMethod.valueOf(payloadEncription), payloadPwd);
                        if (keyInfo.isEncrypted()) {
                            showDialogPasswordPrompt( keyInfo, operation);
                        }
                        else {
                            pascalOperationsService.signAndExecute(operation, keyInfo.getPrivateKey(),keyInfo);
                        }
                        /*PascPrivateKey privateKey=PascPrivateKey.fromPrivateKey(keyInfo.getPrivateKey(),keyInfo.getPascKeyType());
                        byte[] opDigest=operation.generateOpDigest(4.0f);
                        OfflineSignResult res=privateKey.sign(opDigest);
                        String rawOps=operation.getRawOperations(res.getStringR(), res.getStringS());
                        pascalcoinServiceProvider.executeOperations(rawOps,operation);*/
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onBuyAccount(Bundle params, final Account accountToBuy, final Account accountPayer, final PascPublicKey sellerPublicKey) {
        final Double price = params.getDouble(PARAM_PRICE);
        final Double fee = params.getDouble(PARAM_FEE,0.0001);
        final String payload= params.getString(PARAM_PAYLOAD, null);
        final String payloadEncription=params.getString(PARAM_PAYLOAD_ENCRIPTION, "NONE");
        final String payloadPwd=params.getString(PARAM_PAYLOAD_PWD, null);
        final PrivateKeyInfo keyInfo= preferencesService.getSelectedPrivateKeyInfo();

        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle(getString(R.string.buy_account_sale,accountToBuy.getAccount()))
                .setMessage(getString(R.string.buy_acc_confirm, accountToBuy.getAccount(), PascalUtils.calculateChecksum(accountToBuy.getAccount())))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        BuyAccountOperation operation = new BuyAccountOperation( accountPayer.getAccount(), keyInfo.getPascPublicKey(),  accountToBuy.getAccount(), accountToBuy.getSellerAccount(),sellerPublicKey,accountPayer.getnOperation()+1,price, fee,payload!=null?payload.getBytes():"".getBytes(), PayLoadEncryptionMethod.valueOf(payloadEncription), payloadPwd);
                        if (keyInfo.isEncrypted()) {
                            showDialogPasswordPrompt( keyInfo, operation);
                        }
                        else {
                            pascalOperationsService.signAndExecute(operation, keyInfo.getPrivateKey(),keyInfo);
                        }
                        /*PascPrivateKey privateKey=PascPrivateKey.fromPrivateKey(keyInfo.getPrivateKey(),keyInfo.getPascKeyType());
                        byte[] opDigest=operation.generateOpDigest(4.0f);
                        OfflineSignResult res=privateKey.sign(opDigest);
                        String rawOps=operation.getRawOperations(res.getStringR(), res.getStringS());
                        pascalcoinServiceProvider.executeOperations(rawOps,operation);*/
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }


    @Override
    public void retryDetermineAccountSeller(Integer accountSeller) {
        pascalcoinServiceProvider.getAccount(accountSeller);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case (SCAN_CODE): {

                if (resultCode == RESULT_OK) {
                    String contents = data.getStringExtra("SCAN_RESULT");
                    if (this.sendAccountFragment != null && sendAccountFragment.isVisible()) {
                        sendAccountFragment.setDestinationAddress(contents);
                    }
                }
                if (resultCode == RESULT_CANCELED) {
                    //handle cancel
                }
                break;
            }
            case (SCAN_PRIVATE_KEY): {
                if (resultCode == RESULT_OK) {
                    String contents = data.getStringExtra("SCAN_RESULT");
                    if (this.privateKeyImportFragment != null && privateKeyImportFragment.isVisible()) {
                        privateKeyImportFragment.setPrivateKeyValue(contents);
                    }
                }
                if (resultCode == RESULT_CANCELED) {
                    //handle cancel
                }
                break;
            }
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String textFound=result.get(0);
                    if (this.searchAccountFragment!=null && searchAccountFragment.isVisible()) {
                        searchAccountFragment.setSearchText(textFound);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onAccountSelected(Account account, Boolean sale) {
        if (Boolean.TRUE.equals(sale) && AccountState.LISTED.equals(account.getState())) {
            FragmentManager manager = getSupportFragmentManager();
            buyAccountFragment = BuyAccountFragment.newInstance(account, userAccounts);
            pascalcoinServiceProvider.getAccount(account.getSellerAccount());
            buyAccountFragment.show(manager, "fragment_buy_account");

        } else {
            pascalcoinServiceProvider.getAccountOperations(account.getAccount(), 0, 100);
        }
    }

    @Override
    public void onFireAccountSearch(String name, Integer number, Boolean exact,Boolean forSale, Double minBalance, Double maxBalance, SearchAccountFragment searchAccountFragment) {
            this.searchAccountFragmentResult = searchAccountFragment;
            if (pascalcoinServiceProvider==null) {
                Log.e(TAG,"pascalcoinServiceProvider is null!");
                return;
            }
            if (number!=null) {
                if (exact)
                    pascalcoinServiceProvider.getAccount(number);
                else
                    pascalcoinServiceProvider.findAccounts(null,null,forSale,null,minBalance,maxBalance,number,numAccountsToRetrive.get());
            }
            else
                pascalcoinServiceProvider.findAccounts(name,null,forSale,null,minBalance,maxBalance,0,numAccountsToRetrive.get());
    }

    @Override
    public void onFireAdvancedSearch() {
        loadAdvancedSearchFragment();
    }

    @Override
    public void onKeySelectedInteraction(final PrivateKeyInfo keyInfo, int operation) {
        switch (operation) {
            case OPERATION_DELETE:
                new android.support.v7.app.AlertDialog.Builder(this).setTitle(getString(R.string.txt_delete_key)).setMessage(getString(R.string.txt_delete_key_confirm, keyInfo.getName())).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        preferencesService.deletePrivateKey(keyInfo);
                        loadKeysFragment();
                    }
                }).setNegativeButton(android.R.string.no, null).show();
                break;
            case OPERATION_EDIT:
                if (keyInfo.isEncrypted()) {
                    showDialogPasswordPrompt( keyInfo, null);
                }
                else {
                    loadFormNewKey(keyInfo, null);
                }
                break;
            case OPERATION_ADD:
                loadFormNewKey(null, null);
                break;
            case OPERATION_IMPORT:
                loadFormImportKey();
            default: break;
        }
    }

    public void showDialogPasswordPrompt(final PrivateKeyInfo keyInfo, final PascOperation operation) {
        if (!keyInfo.isEncrypted())
            return;
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View passwordPrompt = li.inflate(R.layout.password_prompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccountActivity.this);
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(passwordPrompt);

        final EditText userInput = passwordPrompt
                .findViewById(R.id.editTextPassword);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                try {
                                    String password = userInput.getText().toString();
                                    String privateKey=OpenSslAes.decrypt(password, keyInfo.getPrivateKey());
                                    if (privateKey==null) throw new Exception("Invalid password");
                                    if (operation!=null) {
                                        pascalOperationsService.signAndExecute(operation, privateKey, keyInfo);
                                    } else {
                                        keyInfo.setPrivateKey(privateKey);
                                        EventBus.getDefault().post(new KeyDecodedEvent(keyInfo, password));
                                    }
                                    dialog.dismiss();
                                } catch(Exception ex) {
                                    Toast.makeText(AccountActivity.this,"Invalid password",Toast.LENGTH_LONG);
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    @Override
    public void onNodeSelectedInteraction(final NodeInfo nodeInfo, int operation) {
        switch (operation) {
            case OPERATION_NODE_DELETE:
            new android.support.v7.app.AlertDialog.Builder(this).setTitle(getString(R.string.txt_delete_node)).setMessage(getString(R.string.delete_node_confirm, nodeInfo.getName())).setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    preferencesService.deleteNodeInfo(nodeInfo);
                    loadNodesFragment();
                }
            }).setNegativeButton(android.R.string.no, null).show();
                break;
            case OPERATION_NODE_EDIT: break;
            case OPERATION_NODE_ADD:
                loadFormNewNode();
                break;
            default: break;
        }
    }

    public void loadFormNewNode() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag("fragment_add_node");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        if (nodeAddFragment == null) nodeAddFragment = NodeAddFragment.newInstance(preferencesService);
        nodeAddFragment.show(manager, "fragment_add_node");
    }


    public void loadFormNewKey(PrivateKeyInfo keyInfo, String password) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag("fragment_add_key");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        if (keyInfo==null) {
            PrivateKeyAddFragment privateKeyAddFragment = PrivateKeyAddFragment.newInstance(preferencesService);
            privateKeyAddFragment.show(manager, "fragment_add_key");
        } else {
            EditPascalcoinKeyFragment editPascalcoinKeyFragment= EditPascalcoinKeyFragment.newInstance(preferencesService, keyInfo, password);
            editPascalcoinKeyFragment.show(manager, "fragment_add_key");
        }
    }

    public void loadFormImportKey() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentByTag("fragment_import_key");
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        privateKeyImportFragment= PrivateKeyImportFragment.newInstance(preferencesService);
        privateKeyImportFragment.show(manager, "fragment_import_key");
    }

    private boolean canUseFingerprint() {

        Boolean fingerPrint=false;
        KeyguardManager keyguardManager;
        FingerprintManager fingerprintManager;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerPrint=true;
            //Get an instance of KeyguardManager and FingerprintManager//
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

            //Check whether the device has a fingerprint sensor//
            if (!fingerprintManager.isHardwareDetected()) {
                fingerPrint=false;
            }
            //Check whether the user has granted your app the USE_FINGERPRINT permission//
            if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                // If your app doesn't have this permission, then display the following text//
                Toast.makeText(this.getApplicationContext(), "Please enable the fingerprint permission", Toast.LENGTH_LONG).show();
                fingerPrint=false;
            }

            //Check that the user has registered at least one fingerprint//
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                // If the user hasn’t configured any fingerprints, then display the following message//
                Toast.makeText(this.getApplicationContext(), "No fingerprint configured. Please register at least one fingerprint in your device's Settings", Toast.LENGTH_LONG).show();
                fingerPrint=false;
            }
        }
        return fingerPrint;
    }


    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class GeneralPreferenceFragment extends PreferenceFragmentCompat {

        private Boolean fingerPrint;

        public static  GeneralPreferenceFragment newInstance(Boolean canUseFingerprint) {
            GeneralPreferenceFragment generalPreferenceFragment=new GeneralPreferenceFragment();
            generalPreferenceFragment.fingerPrint=canUseFingerprint;
            return generalPreferenceFragment;
        }

        public GeneralPreferenceFragment() {

        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference(DECRYPT_PASSWORDS));
            bindPreferenceSummaryToValue(findPreference(SELECTED_CURRENCY));
            bindPreferenceSummaryToValue(findPreference(SELECTED_PUBLIC_KEY));
            bindPreferenceSummaryToValue(findPreference(SELECTED_NODE));
            bindPreferenceSummaryToValue(findPreference(NUM_ACCOUNTS));
            if (fingerPrint)
                bindPreferenceSummaryToValue(findPreference(APPLICATION_FINGERPRINT));
            else {
                Preference preference=findPreference(APPLICATION_FINGERPRINT);
                preference.setEnabled(false);
                //PreferenceGroup preferenceParent = preference.getParent();
                //preferenceParent.removePreference(preference);
            }
        }

        @Override
        public void onDisplayPreferenceDialog(final Preference preference) {
            if (preference instanceof NodeListPreferences || preference instanceof KeysListPreferences) {
                final ListPreference pref=(ListPreference) preference;
                android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(getContext());
                builder.setTitle(pref.getTitle());
                ListPreferenceDialogClickListener listPreferenceDialogClickListener=new ListPreferenceDialogClickListener(pref);
                builder.setNegativeButton(pref.getNegativeButtonText(), listPreferenceDialogClickListener);
                builder.setNeutralButton(pref.getPositiveButtonText(), listPreferenceDialogClickListener);

                builder.setSingleChoiceItems(pref.getEntries(), pref.findIndexOfValue(pref.getValue()), listPreferenceDialogClickListener);
                builder.show();
            } else {
                super.onDisplayPreferenceDialog(preference);
            }
        }

    }

    private static class ListPreferenceDialogClickListener implements DialogInterface.OnClickListener {

        private ListPreference pref;

        public ListPreferenceDialogClickListener(ListPreference preference) {
            this.pref=preference;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which == -3) {
                if (SELECTED_PUBLIC_KEY.equals(pref.getKey())) {
                    EventBus.getDefault().post(new org.pascalcoin.pascalcoinofficial.event.NewKeyEvent());
                }
                else if (SELECTED_NODE.equals(pref.getKey())) {
                    EventBus.getDefault().post(new org.pascalcoin.pascalcoinofficial.event.NewNodeEvent());
                }
            }
            else if (which >=0) {
                pref.setValue(pref.getEntryValues()[which]+"");
                if (pref.getOnPreferenceClickListener()!=null)
                    pref.getOnPreferenceClickListener().onPreferenceClick(pref);
                if (pref.getOnPreferenceChangeListener()!=null)
                    pref.getOnPreferenceChangeListener().onPreferenceChange(pref,pref.getValue());
                pref.callChangeListener(pref.getValue());
            }
            dialog.dismiss();
        }
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        String stringValue="";
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        // Trigger the listener immediately with the preference's
        // current value.
        //sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, preference.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(preference.getKey(), ""));
        if (preference.getKey().equals(NUM_ACCOUNTS)) {
            stringValue=String.valueOf(preference.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(preference.getKey(), NUM_ACCOUNTS_DEFAULT));
        } else if (preference.getKey().equals(APPLICATION_FINGERPRINT)) {
            stringValue=String.valueOf(preference.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(preference.getKey(), false));
        }
        else {
            stringValue = preference.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(preference.getKey(), "");
        }
        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);


        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.setSummary(stringValue);
        }
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);


            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            EventBus.getDefault().post(new org.pascalcoin.pascalcoinofficial.event.PreferenceChangedEvent(preference,stringValue));
            return true;
        }
    };

}
