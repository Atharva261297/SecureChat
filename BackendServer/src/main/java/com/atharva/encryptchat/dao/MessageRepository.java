package com.atharva.encryptchat.dao;

import com.atharva.encryptchat.model.Account;
import com.atharva.encryptchat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
