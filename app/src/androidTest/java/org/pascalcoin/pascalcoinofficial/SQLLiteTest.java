package org.pascalcoin.pascalcoinofficial;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;
import android.test.RenamingDelegatingContext;

import org.pascalcoin.pascalcoinofficial.data.SqlDatabaseHelper;
import org.pascalcoin.pascalcoinofficial.model.NodeInfo;
import org.pascalcoin.pascalcoinofficial.model.PrivateKeyInfo;
import com.github.davidbolet.jpascalcoin.common.model.KeyType;
import com.github.davidbolet.jpascalcoin.crypto.model.PascPrivateKey;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.Security;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getContext;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SQLLiteTest {

    private SqlDatabaseHelper mDataSource;

    @Before
    public void setUp(){
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        mDataSource = new SqlDatabaseHelper(context);
    }

    @After
    public void finish() {
        mDataSource.close();
    }



    @Test
    public void saveAndRetrieveClearPrivateKey() throws Exception {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
        PascPrivateKey privateKey = PascPrivateKey.generate(KeyType.SECP256K1);

        mDataSource.addNewPrivateKey(privateKey, "testKey1",  null);
        List<PrivateKeyInfo> keys=mDataSource.getPrivateKeysInfo();
        assertTrue(keys.size()>0 && keys.get(0).getName().equals("testKey1") && keys.get(0).getPascKeyType().equals(KeyType.SECP256K1));
        PascPrivateKey privateKey2 = mDataSource.getByPublicKey(privateKey.getPublicKey(),null);
        assertTrue (privateKey2.getPrivateKey().equals(privateKey.getPrivateKey()));

    }


    @Test
    public void saveAndRetrieveEncryptedPrivateKey() throws Exception {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
        PascPrivateKey privateKey = PascPrivateKey.generate(KeyType.SECP256K1);

        mDataSource.addNewPrivateKey(privateKey, "testKey1",  "123456");
        List<PrivateKeyInfo> keys=mDataSource.getPrivateKeysInfo();
        assertTrue(keys.size()>0 );
        assertTrue(keys.get(0).getName().equals("testKey1"));
        assertTrue(keys.get(0).getPascKeyType().equals(KeyType.SECP256K1));

        PascPrivateKey privateKey2 = mDataSource.getByPublicKey(privateKey.getPublicKey(),"123456");
        assertTrue (privateKey2.getPrivateKey().equals(privateKey.getPrivateKey()));

    }


    @Test
    public void addListCountDeleteNodes() throws Exception {
        String nodeName="testxxx";
        long numNodes=mDataSource.countNodes();
        mDataSource.addNode(nodeName,"test",false);
        assertTrue (mDataSource.countNodes()>numNodes);
        List<NodeInfo> nodes=mDataSource.getAllNodes();
        assertTrue (nodes.size()==1);
        mDataSource.deleteNode(nodeName);
        assertTrue (mDataSource.countNodes()==numNodes);
    }


}
