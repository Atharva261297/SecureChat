package com.atharva.encryptchat.controller;

import com.atharva.encryptchat.model.Account;
import com.atharva.encryptchat.model.Friend;
import com.atharva.encryptchat.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class AccountController {

    @Autowired
    private AccountService service;

    @PostMapping("/add")
    public void add(@RequestBody Account account) {
        service.add(account);
    }

    @GetMapping("/getKey")
    public String getKey (@RequestParam String phoneNo) {
        return service.getPublicKey(phoneNo);
    }

    @PostMapping("/syncKeys")
    public List<Friend> getKeys(@RequestBody List<Friend> phoneNos) {
        return service.getPublicKeys(phoneNos);
    }

    @PostMapping("/remove")
    public void remove(@RequestParam String phoneNo) {
        service.remove(phoneNo);
    }
}
