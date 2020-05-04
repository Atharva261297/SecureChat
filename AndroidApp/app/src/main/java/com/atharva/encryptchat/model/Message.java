package com.atharva.encryptchat.model;

import com.atharva.encryptchat.data.RuntimeData;
import com.atharva.encryptchat.utils.RSA;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

    private String receiver;
    private String sender;
    private String message;
    private Date time;
    private boolean read;

    public Message() {
    }

    public Message(@NotNull Message message, Boolean read) {
        this.receiver = message.receiver;
        this.sender = message.sender;
        this.message = message.message;
        this.time = message.time;
        this.read = read;
    }

    public Message(String receiver, String sender, String message, Date time, boolean read) {
        this.receiver = receiver;
        this.sender = sender;
        this.message = message;
        this.time = time;
        this.read = read;
    }

    public Message(@NotNull Message message) {
        this.receiver = message.receiver;
        this.sender = message.sender;
        this.message = message.message;
        this.time = message.time;
        this.read = message.read;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Message encrypted(String publicKey) {
        RSA rsaInstance = RSA.Companion.getInstance();
        String encryptedMessageText =
                rsaInstance.encrypt(this.message, rsaInstance.getKey(publicKey));
        return new Message(this.receiver, this.sender, encryptedMessageText, this.time, this.read);
    }

    public Message decrypted() {
        RSA rsaInstance = RSA.Companion.getInstance();
        String decryptedMessageText =
                rsaInstance.decrypt(this.message, RuntimeData.Companion.getPrivate());
        return new Message(this.receiver, this.sender, decryptedMessageText, this.time, this.read);
    }
}
