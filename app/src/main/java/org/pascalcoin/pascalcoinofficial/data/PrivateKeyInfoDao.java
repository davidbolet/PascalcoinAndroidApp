package org.pascalcoin.pascalcoinofficial.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.pascalcoin.pascalcoinofficial.model.PrivateKeyInfo;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface PrivateKeyInfoDao {
    @Query("SELECT * FROM "+Constants.KEYS_TABLE)
    List<PrivateKeyInfo> getAll();

    @Query("SELECT * FROM "+Constants.KEYS_TABLE+" WHERE name LIKE :name ")
    PrivateKeyInfo findByName(String name);

    @Query("SELECT COUNT(*) FROM "+Constants.KEYS_TABLE)
    Integer getRowCount();

    @Insert
    void insertAll(PrivateKeyInfo...privateKeyInfos);

    @Update(onConflict = REPLACE)
    void update(PrivateKeyInfo privateKeyInfo);

    @Delete
    void delete(PrivateKeyInfo privateKeyInfo);
}
