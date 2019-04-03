package org.pascalcoin.pascalcoinofficial.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.github.davidbolet.jpascalcoin.common.exception.UnsupportedKeyTypeException;
import com.github.davidbolet.jpascalcoin.common.model.KeyType;
import com.github.davidbolet.jpascalcoin.crypto.model.PascPrivateKey;

import org.greenrobot.eventbus.EventBus;
import org.pascalcoin.pascalcoinofficial.R;
import org.pascalcoin.pascalcoinofficial.event.KeyAddedEvent;
import org.pascalcoin.pascalcoinofficial.event.KeyDeletedEvent;
import org.pascalcoin.pascalcoinofficial.event.KeyUpdatedEvent;
import org.pascalcoin.pascalcoinofficial.event.NodeAddedEvent;
import org.pascalcoin.pascalcoinofficial.event.NodeDeletedEvent;
import org.pascalcoin.pascalcoinofficial.model.NodeInfo;
import org.pascalcoin.pascalcoinofficial.model.PrivateKeyInfo;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class DatabaseHelper {

    public static final String DEFAULT_PUBLIC_KEY_NAME="default";
    public static final String PASCALCOIN_ASIA_APP_BASE_URL = "https://asia-1.pascalcoin.app";
    public static final String PASCALCOIN_EUROPE_APP_BASE_URL = "https://europe-1.pascalcoin.app";
    public static final String PASCALCOIN_APP_ASIA_DEFAULT_NODE_NAME="PascAsia";
    public static final String PASCALCOIN_APP_EUROPE_DEFAULT_NODE_NAME="PascEurope";

    private static DatabaseHelper _instance;

    private PascalcoinDatabase pascalcoinDatabase;
    private NodeInfoDao nodeInfoDao;
    private PrivateKeyInfoDao privateKeyInfoDao;

    public static DatabaseHelper getInstance(Context context) {
        if (_instance==null)
            _instance=new DatabaseHelper(context);
        return _instance;
    }

    public NodeInfoDao getNodeInfoDao() {
        return nodeInfoDao;
    }

    public PrivateKeyInfoDao getPrivateKeyInfoDao() {
        return privateKeyInfoDao;
    }

    public List<PrivateKeyInfo> getPrivateKeyInfos() {

        return privateKeyInfoDao.getAll();
    }

    public List<NodeInfo> getNodeInfos() {
        return nodeInfoDao.getAll();
    }

    private DatabaseHelper(Context context) {
        pascalcoinDatabase = Room.databaseBuilder(context, PascalcoinDatabase.class, Constants.DB_NAME).fallbackToDestructiveMigration().allowMainThreadQueries().build();
        nodeInfoDao=pascalcoinDatabase.nodeInfoDao();
        privateKeyInfoDao=pascalcoinDatabase.privateKeyInfoDao();
        checkInitialNodes();
        checkInitialKeys();
    }

    public PascalcoinDatabase getPascalcoinDatabase() {
        return this.pascalcoinDatabase;
    }


    public void checkInitialKeys() {
        CheckInitialKeysTask checkInitialKeysTask=new CheckInitialKeysTask(privateKeyInfoDao);
        checkInitialKeysTask.execute(new PrivateKeyInfo());
    }

    public void insertKeys(PrivateKeyInfo ... privateKeyInfos) {
        InsertKeyTask insertKeyTask=new InsertKeyTask(privateKeyInfoDao);
        insertKeyTask.execute(privateKeyInfos);
    }

    public void updateKey(PrivateKeyInfo privateKeyInfo) {
        UpdateKeyTask updateKeyTask=new UpdateKeyTask(privateKeyInfoDao);
        updateKeyTask.execute(privateKeyInfo);
    }

    public void deleteKey(PrivateKeyInfo toDelete) {
        DeleteKeyTask deleteKeyTask=new DeleteKeyTask(privateKeyInfoDao);
        deleteKeyTask.execute(toDelete);
    }

    public PrivateKeyInfo getKeyByName(String name) {
        LoadKeyTask loadKeyTask=new LoadKeyTask(privateKeyInfoDao);
        loadKeyTask.execute(name);
        try {
            return loadKeyTask.get();
        } catch (InterruptedException | ExecutionException e) {}
        return null;
    }

    private boolean isChinese() {
        return Locale.getDefault().getLanguage().startsWith("zh");
    }

    public void checkInitialNodes() {
        CheckInitialNodesTask checkInitialNodesTask=new CheckInitialNodesTask(nodeInfoDao);
        NodeInfo nodeEurope=new NodeInfo(PASCALCOIN_APP_EUROPE_DEFAULT_NODE_NAME,  PASCALCOIN_EUROPE_APP_BASE_URL, true);
        NodeInfo nodeChina=new NodeInfo(PASCALCOIN_APP_ASIA_DEFAULT_NODE_NAME,PASCALCOIN_ASIA_APP_BASE_URL, true);
        if (isChinese())
            checkInitialNodesTask.execute(nodeChina,nodeEurope);
        else
            checkInitialNodesTask.execute(nodeEurope,nodeChina);
    }

    public void insertNodes(NodeInfo... nodeInfos) {
        InsertNodesTask insertNodesTask=new InsertNodesTask(nodeInfoDao);
        insertNodesTask.execute(nodeInfos);
    }

    public void deleteNode(NodeInfo toDelete) {
        DeleteNodeTask deleteNodeTask=new DeleteNodeTask(nodeInfoDao);
        deleteNodeTask.execute(toDelete);
    }

    public NodeInfo getNodeInfoByName(String name) {
        LoadNodeTask loadNodeTask=new LoadNodeTask(nodeInfoDao);
        loadNodeTask.execute(name);
        try {
            return loadNodeTask.get();
        } catch (InterruptedException | ExecutionException e) {}
        return null;
    }


    private static class DeleteNodeTask extends AsyncTask<NodeInfo, Void, Void> {

        private NodeInfoDao mAsyncTaskDao;
        private String deletedNodeName;

        DeleteNodeTask(NodeInfoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final NodeInfo... nodeInfo) {
            deletedNodeName=nodeInfo[0].getName();
            mAsyncTaskDao.delete(nodeInfo[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            EventBus.getDefault().post(new NodeDeletedEvent(deletedNodeName));
        }
    }

    private static class CheckInitialNodesTask extends AsyncTask<NodeInfo, Void, Void> {

        private NodeInfoDao mAsyncTaskDao;

        CheckInitialNodesTask(NodeInfoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final NodeInfo... nodeInfo) {
            if (mAsyncTaskDao.getRowCount()==null || mAsyncTaskDao.getRowCount()==0) {
                mAsyncTaskDao.insertAll(nodeInfo);
            }
            return null;
        }
    }

    private static class InsertNodesTask extends AsyncTask<NodeInfo, Void, Void> {

        private NodeInfoDao mAsyncTaskDao;
        private NodeInfo inserted;

        InsertNodesTask(NodeInfoDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final NodeInfo... nodeInfo) {
            mAsyncTaskDao.insertAll(nodeInfo);
            inserted=nodeInfo[0];
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            EventBus.getDefault().post(new NodeAddedEvent(inserted));

        }

    }

    private static class LoadNodeTask extends AsyncTask<String, Void, NodeInfo> {

        private NodeInfoDao mAsyncTaskDao;

        public LoadNodeTask(NodeInfoDao dao) {
            mAsyncTaskDao = dao;
        }


        @Override
        protected NodeInfo doInBackground(String... strings) {
            return mAsyncTaskDao.findByName(strings[0]);
        }
    }

    private static class CheckInitialKeysTask extends AsyncTask<PrivateKeyInfo, Void, Void> {

        private final PrivateKeyInfoDao privateKeyInfoDao;

        public CheckInitialKeysTask(PrivateKeyInfoDao privateKeyInfoDao) {
            this.privateKeyInfoDao=privateKeyInfoDao;
        }

        @Override
        protected Void doInBackground(PrivateKeyInfo... privateKeyInfos) {
            if (privateKeyInfoDao.getRowCount()==null || privateKeyInfoDao.getRowCount()==0) {
                try {
                    PascPrivateKey privateKey = PascPrivateKey.generate(KeyType.SECP256K1);
                    PrivateKeyInfo newKey=new PrivateKeyInfo(DEFAULT_PUBLIC_KEY_NAME, false, privateKey.getPrivateKey(), privateKey.getPublicKey(), privateKey.getKeyType());
                    privateKeyInfoDao.insertAll(newKey);
                }
                catch(UnsupportedKeyTypeException ue) {
                    Log.e("PREFERENCESSERVICE", ue.getMessage()); //this should never happen
                }
            }
            return null;
        }
    }

    private static class InsertKeyTask extends AsyncTask<PrivateKeyInfo, Void, Void> {

        private final PrivateKeyInfoDao privateKeyInfoDao;
        private PrivateKeyInfo firstAdded;

        public InsertKeyTask(PrivateKeyInfoDao privateKeyInfoDao) {
            this.privateKeyInfoDao=privateKeyInfoDao;
        }

        @Override
        protected Void doInBackground(PrivateKeyInfo... privateKeyInfos) {
            firstAdded=privateKeyInfos[0];
            privateKeyInfoDao.insertAll(privateKeyInfos);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            EventBus.getDefault().post(new KeyAddedEvent(firstAdded));
        }
    }

    private static class UpdateKeyTask extends AsyncTask<PrivateKeyInfo, Void, Void> {

        private final PrivateKeyInfoDao privateKeyInfoDao;
        private PrivateKeyInfo privateKeyInfo;

        public UpdateKeyTask(PrivateKeyInfoDao privateKeyInfoDao) {
            this.privateKeyInfoDao=privateKeyInfoDao;
        }

        @Override
        protected Void doInBackground(PrivateKeyInfo... privateKeyInfos) {
            privateKeyInfo =privateKeyInfos[0];
            privateKeyInfoDao.update(privateKeyInfo);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            EventBus.getDefault().post(new KeyUpdatedEvent(privateKeyInfo));
        }
    }

    private static class DeleteKeyTask extends AsyncTask<PrivateKeyInfo, Void, Void> {

        private final PrivateKeyInfoDao privateKeyInfoDao;
        private String deletedKeyName;

        public DeleteKeyTask(PrivateKeyInfoDao privateKeyInfoDao) {
            this.privateKeyInfoDao=privateKeyInfoDao;
        }

        @Override
        protected Void doInBackground(PrivateKeyInfo... privateKeyInfo) {
            deletedKeyName=privateKeyInfo[0].getName();
            privateKeyInfoDao.delete(privateKeyInfo[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            EventBus.getDefault().post(new KeyDeletedEvent(deletedKeyName));
        }
    }

    private static class LoadKeyTask extends AsyncTask<String, Void, PrivateKeyInfo> {

        private final PrivateKeyInfoDao privateKeyInfoDao;

        public LoadKeyTask(PrivateKeyInfoDao privateKeyInfoDao) {
            this.privateKeyInfoDao=privateKeyInfoDao;
        }

        @Override
        protected PrivateKeyInfo doInBackground(String... params) {
            return privateKeyInfoDao.findByName(params[0]);
        }
    }

}
