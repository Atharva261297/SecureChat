package com.atharva.encryptchat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friend implements Serializable {

    private String name;
    private String phoneNo;
    private String publicKey;

}
