package org.pascalcoin.pascalcoinofficial.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import org.pascalcoin.pascalcoinofficial.model.NodeInfo;

import java.util.List;

@Dao
public interface NodeInfoDao {

    @Query("SELECT * FROM "+Constants.NODES_TABLE)
    List<NodeInfo> getAll();

    @Query("SELECT * FROM "+Constants.NODES_TABLE+" WHERE name LIKE :name ")
    NodeInfo findByName(String name);

    @Query("SELECT COUNT(*) FROM "+Constants.NODES_TABLE)
    Integer getRowCount();

    @Insert
    void insertAll(NodeInfo... nodeInfos);

    @Delete
    void delete(NodeInfo nodeInfo);
}
