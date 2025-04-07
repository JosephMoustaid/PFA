package spring.bricole.service;

import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import spring.bricole.dto.ChatMessageDTO;
import spring.bricole.model.Conversation;
import spring.bricole.model.Message;
import spring.bricole.repository.MessageRepository;

import java.time.LocalDateTime;

@Service
public class MessageService {

    MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    // savve the message
    public void saveMessage(Message message){
        messageRepository.save(message);
    }

}
