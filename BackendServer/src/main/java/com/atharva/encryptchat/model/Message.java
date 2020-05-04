package com.atharva.encryptchat.model;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages", schema = "dev")
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Expose(serialize = false, deserialize = false)
    private Long id;

    @Column
    private String receiver;
    @Column
    private String sender;
    @Column(length = 500)
    private String message;
    @Column
    private Date time;
    @Column
    private boolean read;

}
