package spring.bricole.controller;

import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import spring.bricole.dto.ChatMessageDTO;
import spring.bricole.model.Conversation;
import spring.bricole.model.User;
import spring.bricole.repository.ConversationRepository;
import spring.bricole.service.ConversationService;
import spring.bricole.service.MessageService;

import java.time.LocalDateTime;

import spring.bricole.dto.ChatNotificationDTO;
import spring.bricole.dto.ChatMessageResponseDTO;
import spring.bricole.dto.ChatTypingDTO;

import spring.bricole.model.Message;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final ConversationRepository conversationRepository;
    private final ConversationService conversationService;

    public ChatController(SimpMessagingTemplate messagingTemplate,
                          MessageService messageService, ConversationRepository conversationRepository, ConversationService conversationService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.conversationRepository = conversationRepository;
        this.conversationService = conversationService;
    }

    @Transactional
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDTO chatMessage) {
        // Save the message to database
        Conversation conversation = conversationRepository.findById(chatMessage.getRoom())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        // instantiate a new message
        Message message = new Message();
        message.setContent(chatMessage.getContent());
        User user = conversation.getUser1();
        if(user.getId() != chatMessage.getSenderId()) {
            user = conversation.getUser2();
        }
        message.setSender(user);
        message.setSendAt(LocalDateTime.now());
        message.setRead(false);
        message.setSent(true);


        // add the message to the conversation
        conversation.addMessage(message);

        // Update conversation last message info
        conversation.setLastMessage(chatMessage.getContent());
        conversation.setLastMessageAt(LocalDateTime.now());

        // Save the message first
        messageService.saveMessage(message);

        // Save Conversation
        conversationService.updateConversation(conversation);

        // Prepare response DTO with additional info
        ChatMessageResponseDTO response = new ChatMessageResponseDTO(
                message.getId(),
                conversation.getId(),
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                message.getContent(),
                message.getSendAt()
        );

        // Send message to specific room
        messagingTemplate.convertAndSend("/topic/room/" + chatMessage.getRoom(), response);
    }

    @MessageMapping("/chat.joinRoom")
    public void joinRoom(@Payload ChatMessageDTO chatMessage) {
        // Notify that user has joined
        ChatNotificationDTO notification = new ChatNotificationDTO(
                chatMessage.getSenderId() ,
                chatMessage.getSenderFirstName() + " " + chatMessage.getSenderLastName() + " has joined the chat",
                LocalDateTime.now()
        );

        messagingTemplate.convertAndSend("/topic/room/" + chatMessage.getRoom(), notification);
    }

    @MessageMapping("/chat.typing")
    public void typing(@Payload ChatMessageDTO chatMessage) {
        ChatTypingDTO typingNotification = new ChatTypingDTO(
                chatMessage.getSenderId(),
                chatMessage.getSenderFirstName(),
                chatMessage.getSenderLastName(),
                true // typing status
        );
        messagingTemplate.convertAndSend("/topic/typing/" + chatMessage.getRoom(), typingNotification);
    }
}




