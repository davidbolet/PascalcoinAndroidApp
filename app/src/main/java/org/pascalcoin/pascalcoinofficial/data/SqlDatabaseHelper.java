package org.pascalcoin.pascalcoinofficial.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.pascalcoin.pascalcoinofficial.model.NodeInfo;
import org.pascalcoin.pascalcoinofficial.model.PrivateKeyInfo;

import com.github.davidbolet.jpascalcoin.common.helper.OpenSslAes;
import com.github.davidbolet.jpascalcoin.common.model.KeyType;
import com.github.davidbolet.jpascalcoin.common.model.PascPublicKey;
import com.github.davidbolet.jpascalcoin.crypto.model.PascPrivateKey;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by davidbolet on 19/1/18.
 */

public class SqlDatabaseHelper extends SQLiteOpenHelper {

    Context ctx;

    public SqlDatabaseHelper(Context context) {
        super(context, Constants.DB_NAME,null, Constants.DB_VERSION);
        this.ctx=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


       String sql = "CREATE TABLE IF NOT EXISTS "+Constants.KEYS_TABLE
               +" ( " +
               "name VARCHAR, " +
               "keyType VARCHAR, " +
               "privateKey VARCHAR," +
               "publicKey VARCHAR PRIMARY KEY," +
               "is_encrypted INTEGER NOT NULL DEFAULT 0, "+
               "email VARCHAR" +
               ")";
       db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS "+Constants.NODES_TABLE
                +" ( " +
                "name VARCHAR PRIMARY KEY, " +
                "url VARCHAR , " +
                "is_ssl INTEGER NOT NULL DEFAULT 0 " +
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE "+Constants.NODES_TABLE);
        onCreate(db);
    }



    /*************************************** private keys functions **************************************/

    /**
     * If password is provided, private key will be saved encrypted
     * @param privateKey PascPrivateKey key to save
     * @param name String name given to the key
     * @param password String pasword user to encrypt key
     */
    public void addNewPrivateKey(PascPrivateKey privateKey, String name, String password) {
        ContentValues values = new ContentValues();
        values.put("keyType",privateKey.getKeyType().name());

        if (password!=null) {
            try {
                values.put("privateKey", OpenSslAes.encrypt(password, privateKey.getPrivateKey()));
            } catch(Exception e) {
                Log.e("ENCRYPTION",e.getMessage());
            }
        }
        else
            values.put("privateKey",privateKey.getPrivateKey());
        values.put("is_encrypted",password!=null);
        values.put("publicKey",privateKey.getPublicKey().getEncPubKey());
        values.put("name",name);
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.insert(Constants.KEYS_TABLE, null, values);
        db.endTransaction();
        Log.d(SqlDatabaseHelper.class.getSimpleName(),String.format("Private key with name %s saved",name));
    }

    /**
     * Returns private key based on given public key. Decrypts key i
     * @param publicKey
     * @param password
     * @return
     */
    public PascPrivateKey getByPublicKey(PascPublicKey publicKey, String password) {
        PascPrivateKey result = null;
        String privateKey=null;
        boolean isEncripted=false;
        KeyType keyType=null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.KEYS_TABLE, new String[] {"publicKey" ,"name","keyType","privateKey", "is_encrypted"},"publicKey=? ",new String[] {publicKey.getEncPubKey()},null,null,null);
        if(cursor.moveToFirst()) {
            privateKey =cursor.getString(cursor.getColumnIndex("privateKey"));
            keyType =  KeyType.valueOf(cursor.getString(cursor.getColumnIndex("keyType")));
            isEncripted = cursor.getInt(cursor.getColumnIndex("is_encrypted"))==1;
        }
        if (privateKey!=null && keyType!=null) {
            if (isEncripted && password!=null) {
                privateKey = OpenSslAes.decrypt(password, privateKey);
                if (privateKey == null) throw new IllegalArgumentException("Wrong password provided");
                result = PascPrivateKey.fromPrivateKey(privateKey, keyType);
            }
            else if (!isEncripted){
                result = PascPrivateKey.fromPrivateKey(privateKey, keyType);
            } else
                throw new IllegalArgumentException("Key is encrypted and password was not supplied!");
        }
        cursor.close();
        return result;
    }

   /* public List<PrivateKeyInfo> getPrivateKeysInfoByName(String name) {
        List<PrivateKeyInfo> result=new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.KEYS_TABLE, new String[] {"name","is_encrypted","keyType"},"name=? ",new String[] {name},null,null,null);
        if(cursor.moveToFirst()) {
            //result.add(new PrivateKeyInfo(cursor.getString(cursor.getColumnIndex("name")),(cursor.getInt(cursor.getColumnIndex("is_encrypted"))==1), PascPublicKey.fromEncodedPubKey(cursor.getString(cursor.getColumnIndex("publicKey")))  ,KeyType.valueOf(cursor.getString(cursor.getColumnIndex("keyType")))));
        }
        cursor.close();
        return result;
    }*/

    public List<PrivateKeyInfo> getPrivateKeysInfo() {
        List<PrivateKeyInfo> result=new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Constants.KEYS_TABLE, new String[] {"name","is_encrypted","keyType","publicKey"},null,null,null,null,null);
        if(cursor.moveToFirst()) {
            //result.add(new PrivateKeyInfo(cursor.getString(cursor.getColumnIndex("name")),(cursor.getInt(cursor.getColumnIndex("is_encrypted"))==1), PascPublicKey.fromEncodedPubKey(cursor.getString(cursor.getColumnIndex("publicKey")))  ,KeyType.valueOf(cursor.getString(cursor.getColumnIndex("keyType")))));
        }
        cursor.close();
        return result;
    }

    public long countPrivateKeys() {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db,Constants.KEYS_TABLE);
    }

    /*************************************** nodes functions **************************************/

    public void addNode(String name, String url, boolean isSSL)  {
        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("url",url);
        values.put("is_ssl",isSSL?1:0);

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.insert(Constants.NODES_TABLE, null, values);
        db.endTransaction();
        Log.d(SqlDatabaseHelper.class.getSimpleName(),String.format("Node with name %s and url %s saved",name,url));
    }

    public NodeInfo getByName(String name)  {
        SQLiteDatabase db = this.getReadableDatabase();
        NodeInfo result=null;
        Cursor cursor = db.query(Constants.NODES_TABLE, new String[] {"name","url","is_ssl"},"name=?",new String[] {name},null,null,null);
        if(cursor.moveToFirst()) {
             result=new NodeInfo(cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("url")), (cursor.getInt(cursor.getColumnIndex("is_ssl"))==1));
        }
        cursor.close();
        return result;
    }

    public List<NodeInfo> getAllNodes() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<NodeInfo> result=new ArrayList<>();
        Cursor cursor = db.query(Constants.NODES_TABLE, new String[] {"name","url","is_ssl","is_default"},null,null,null,null,null);
        if(cursor.moveToFirst()) {
            result.add(new NodeInfo(cursor.getString(cursor.getColumnIndex("name")),cursor.getString(cursor.getColumnIndex("url")), (cursor.getInt(cursor.getColumnIndex("is_ssl"))==1) ));
        }
        cursor.close();
        return result;
    }

    public long countNodes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db,Constants.NODES_TABLE);
    }

    public int deleteNode(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Constants.NODES_TABLE, "name=?",new String[] {name});
    }

}
