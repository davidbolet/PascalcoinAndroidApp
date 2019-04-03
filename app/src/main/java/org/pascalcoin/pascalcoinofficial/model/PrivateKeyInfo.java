package org.pascalcoin.pascalcoinofficial.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.github.davidbolet.jpascalcoin.common.model.KeyType;
import com.github.davidbolet.jpascalcoin.common.model.PascPublicKey;

import org.pascalcoin.pascalcoinofficial.data.Constants;

@Entity(tableName = Constants.KEYS_TABLE)
public class PrivateKeyInfo {

    @PrimaryKey
    @NonNull
    String name;
    @ColumnInfo(name = "encrypted")
    boolean encrypted;
    @ColumnInfo(name = "keyType")
    int keyType;
    @ColumnInfo(name = "publicKey")
    String publicKey;
    @ColumnInfo(name = "privateKey")
    String privateKey;

    @Ignore
    public PrivateKeyInfo() {

    }

    public PrivateKeyInfo(String name, boolean encrypted, String publicKey, String privateKey, int keyType) {
        this.name=name;
        this.publicKey=publicKey;
        this.encrypted=encrypted;
        this.keyType=keyType;
        this.privateKey=privateKey;
    }

    public PrivateKeyInfo(String name, boolean isEncrypted, String privateKeyString, PascPublicKey publicKey, KeyType keyType) {
        this.name=name;
        this.publicKey=publicKey.getEncPubKey();
        this.encrypted=isEncrypted;
        this.keyType=keyType.getValue();
        this.privateKey=privateKeyString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public int getKeyType() {
        return keyType;
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public KeyType getPascKeyType() {
        return KeyType.fromValue(this.keyType);
    }

    public PascPublicKey getPascPublicKey() {
        return PascPublicKey.fromEncodedPubKey(this.publicKey);
    }

}
