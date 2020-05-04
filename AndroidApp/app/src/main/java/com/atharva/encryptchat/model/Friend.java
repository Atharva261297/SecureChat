package com.atharva.encryptchat.model;

import com.orm.SugarRecord;

import java.io.Serializable;


public class Friend extends SugarRecord<Friend> implements Serializable {

    private String name;
    private String phoneNo;
    private String publicKey;

    public Friend() {
    }

    public Friend(String name, String phoneNo, String publicKey) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.publicKey = publicKey;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
