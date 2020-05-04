package com.atharva.encryptchat.model;

import java.io.Serializable;

public class Account implements Serializable {

    private String phoneNo;
    private String publicKey;

    public Account() {
    }

    public Account(String phoneNo, String publicKey) {
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
}
