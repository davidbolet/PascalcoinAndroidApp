package org.pascalcoin.pascalcoinofficial.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.pascalcoin.pascalcoinofficial.data.Constants;

import java.util.Objects;

@Entity(tableName = Constants.NODES_TABLE)
public class NodeInfo implements Parcelable {

    @PrimaryKey
    @NonNull
    private String name;

    @ColumnInfo(name = "url")
    private String url;
    @ColumnInfo(name = "is_ssl")
    private boolean isSSL;

    @Ignore
    public NodeInfo() {

    }

    public NodeInfo(String name, String url, boolean isSSL) {
        this();
        this.name=name;
        this.url=url;
        this.isSSL=isSSL;
    }

    protected NodeInfo(Parcel in) {
        name = in.readString();
        url = in.readString();
        isSSL = in.readInt() != 0;
    }

    public static final Creator<NodeInfo> CREATOR = new Creator<NodeInfo>() {
        @Override
        public NodeInfo createFromParcel(Parcel in) {
            return new NodeInfo(in);
        }

        @Override
        public NodeInfo[] newArray(int size) {
            return new NodeInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getName());
        dest.writeString(this.getUrl());
        dest.writeInt(isSSL()?1:0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSSL() {
        return isSSL;
    }

    public void setSSL(boolean SSL) {
        isSSL = SSL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NodeInfo)) return false;
        NodeInfo nodeInfo = (NodeInfo) o;
        return Objects.equals(name, nodeInfo.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, url, isSSL);
    }
}
