package com.atharva.encryptchat.service;

import com.atharva.encryptchat.dao.AccountRepository;
import com.atharva.encryptchat.model.Account;
import com.atharva.encryptchat.model.Friend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountService {

    @Autowired
    private AccountRepository dao;

    public void add(Account account) {
        dao.save(account);
    }

    public String getPublicKey(String phoneNo) {
        if (dao.existsById(phoneNo)) {
            return dao.getOne(phoneNo).getPublicKey();
        } else {
            return null;
        }
    }

    public void remove(String phoneNo) {
        if (dao.existsById(phoneNo)) {
            dao.deleteById(phoneNo);
        }
    }

    public List<Friend> getPublicKeys(List<Friend> phoneNos) {
        List<Friend> list = new ArrayList<>();
        int i=0;
        for (Friend p : phoneNos) {
            System.out.println("index = " + i);
            i += 1;
            String key = getPublicKey(p.getPhoneNo());
            if (key != null) {
                list.add(new Friend(p.getName(), p.getPhoneNo(), key));
            }
        }
        return list;
    }
}
