package org.pascalcoin.pascalcoinofficial.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Objects;

/**
 * Created by davidbolet on 30/1/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Parcelable {

    private String name;
    private String lastName;
    private String alias;
    private String email;
    private String password;
    private boolean storePasswords;
    private boolean isOAuth;
    private boolean receiveNotifications;
    private boolean receiveOffers;
    private boolean allowAutoLogin;
    private Date createdAt;
    @JsonProperty("lastAccess")
    private Date lastAccess;
    @JsonProperty("defaultCurrency")
    private String defaultCurrency;
    @JsonProperty("defaultLocale")
    private String defaultLocale;
    @JsonProperty("b58_pubkey")
    private String b58PubKey;
    @JsonProperty("enc_pubkey")
    private String encPubKey;
    @JsonProperty("passwords")
    private String passwords;

    public User() {
        this.setCreatedAt(new Date());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name)
    {
        this.name=name;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName=lastName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Boolean getStorePasswords() {
        return storePasswords;
    }

    public void setStorePasswords(Boolean storePasswords) {
        this.storePasswords = storePasswords;
    }

    public Boolean getOAuth() {
        return isOAuth;
    }

    public void setOAuth(Boolean OAuth) {
        isOAuth = OAuth;
    }

    public Boolean getReceiveNotifications() {
        return receiveNotifications;
    }

    public void setReceiveNotifications(Boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }

    public Boolean getReceiveOffers() {
        return receiveOffers;
    }

    public void setReceiveOffers(Boolean receiveOffers) {
        this.receiveOffers = receiveOffers;
    }

    public Boolean getAllowAutoLogin() {
        return allowAutoLogin;
    }

    public void setAllowAutoLogin(Boolean allowAutoLogin) {
        this.allowAutoLogin = allowAutoLogin;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
    }

    public String getDefaultCurrency() {
        return defaultCurrency==null?"USD":defaultCurrency;
    }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String getB58PubKey() {
        return b58PubKey;
    }

    public void setB58PubKey(String b58PubKey) {
        this.b58PubKey = b58PubKey;
    }

    public String getEncPubKey() {
        return encPubKey;
    }

    public void setEncPubKey(String encPubKey) {
        this.encPubKey=encPubKey;
    }

    public String getPasswords() {
        return passwords;
    }

    public void setPasswords(String passwords) {
        this.passwords = passwords;
    }

    @Override
    public String toString() {
        return "User{" + ", name='" + name + '\'' +", lastName='" + lastName + '\'' + ", email='" + email + '\'' + ", password='" + password + '\'' + ", storePasswords=" + storePasswords + ", createdAt=" + createdAt + ", lastAccess=" + lastAccess + '}';
    }

    private User(Parcel in) {
        this.name=in.readString();
        this.lastName=in.readString();
        this.alias=in.readString();
        this.email=in.readString();
        this.password=in.readString();
        this.storePasswords = in.readInt() == 1;
        this.isOAuth = in.readInt() == 1;
        this.receiveNotifications = in.readInt() == 1;
        this.receiveOffers = in.readInt() == 1;
        this.allowAutoLogin = in.readInt() == 1;
        this.createdAt= new Date(in.readLong());
        long time=in.readLong();
        this.lastAccess= time== 0?null:new Date(time);
        this.defaultCurrency=in.readString();
        this.defaultLocale=in.readString();
        this.b58PubKey = in.readString();
        this.encPubKey = in.readString();
        this.passwords=in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(this.getName());
        dest.writeString(this.getLastName());
        dest.writeString(this.getAlias());
        dest.writeString(this.getEmail());
        dest.writeString(this.getPassword());
        dest.writeInt(this.getStorePasswords()?1:0);
        dest.writeInt(this.getOAuth()?1:0);
        dest.writeInt(this.getReceiveNotifications()?1:0);
        dest.writeInt(this.getReceiveOffers()?1:0);
        dest.writeInt(this.getAllowAutoLogin()?1:0);
        dest.writeLong(this.getCreatedAt().getTime());
        dest.writeLong(this.getLastAccess()==null?0L:this.getLastAccess().getTime());
        dest.writeString(this.getDefaultCurrency());
        dest.writeString(this.getDefaultLocale());
        dest.writeString(this.getB58PubKey());
        dest.writeString(this.getEncPubKey());
        dest.writeString(this.passwords);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return storePasswords == user.storePasswords && isOAuth == user.isOAuth && receiveNotifications == user.receiveNotifications && receiveOffers == user.receiveOffers && allowAutoLogin == user.allowAutoLogin && Objects.equals(name, user.name) && Objects.equals(lastName, user.lastName) && Objects.equals(alias, user.alias) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(createdAt, user.createdAt) && Objects.equals(lastAccess, user.lastAccess) && Objects.equals(defaultCurrency, user.defaultCurrency) && Objects.equals(defaultLocale, user.defaultLocale) && Objects.equals(b58PubKey, user.b58PubKey) && Objects.equals(encPubKey, user.encPubKey) && Objects.equals(passwords, user.passwords);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, lastName, alias, email, password, storePasswords, isOAuth, receiveNotifications, receiveOffers, allowAutoLogin, createdAt, lastAccess, defaultCurrency, defaultLocale, b58PubKey, encPubKey, passwords);
    }
}
