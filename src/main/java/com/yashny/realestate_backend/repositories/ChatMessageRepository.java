package com.yashny.realestate_backend.repositories;

import com.yashny.realestate_backend.entities.ChatMessage;
import com.yashny.realestate_backend.enums.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Long countBySenderIdAndRecipientIdAndStatus(
            Long senderId, Long recipientId, MessageStatus status);

    List<ChatMessage> findByChatId(String chatId);

    List<ChatMessage> findBySenderIdAndRecipientId(Long senderId, Long recipientId);
}
