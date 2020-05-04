package com.atharva.encryptchat.service;

import com.atharva.encryptchat.dao.MessageRepository;
import com.atharva.encryptchat.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@EnableTransactionManagement
public class MessageService {

    @Autowired
    private MessageRepository dao;

    @Transactional
    public void send(Message message) {
        message.setRead(false);
        dao.save(message);
    }

    @Transactional
    public List<Message> getUnreadMessages(String phoneNo) {
        List<Message> messageList = dao.findAll(Example.of(new Message(null, phoneNo, null, null, null, false)));
        messageList.forEach(m -> {
            m.setRead(true);
            dao.save(m);
        });
        return messageList;
    }
}
