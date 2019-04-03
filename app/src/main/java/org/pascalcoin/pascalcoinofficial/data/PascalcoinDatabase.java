package org.pascalcoin.pascalcoinofficial.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import org.pascalcoin.pascalcoinofficial.model.NodeInfo;
import org.pascalcoin.pascalcoinofficial.model.PrivateKeyInfo;

@Database(entities = {NodeInfo.class, PrivateKeyInfo.class}, version = Constants.DB_VERSION, exportSchema = false)
public abstract class PascalcoinDatabase extends RoomDatabase {

    public abstract NodeInfoDao nodeInfoDao();

    public abstract PrivateKeyInfoDao privateKeyInfoDao();
}
