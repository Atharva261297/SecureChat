package com.atharva.encryptchat.controller;

import com.atharva.encryptchat.model.Message;
import com.atharva.encryptchat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public void send(@RequestBody Message message) {
        messageService.send(message);
    }

    @GetMapping("/receive")
    public List<Message> receive(@RequestParam String phoneNo) {
        System.out.println("messages for " + phoneNo);
        return messageService.getUnreadMessages(phoneNo);
    }
}
