package com.atharva.encryptchat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.security.PublicKey;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account", schema = "dev")
public class Account {

    @Id
    @Column(name = "phone_no")
    private String phoneNo;

    @Column(name = "public_key", length = 400)
    private String publicKey;

}
