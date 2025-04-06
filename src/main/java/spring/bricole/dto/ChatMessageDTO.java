package spring.bricole.dto;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.bricole.model.User;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChatMessageDTO {
    private MessageType type;
    private String content;
    private int senderId;
    private String senderFirstName;
    private String senderLastName;
    // This field is used to identify the conversation
    private int room;
    private LocalDateTime sendAt;
    private boolean isRead;
    private boolean isSent;
    public enum MessageType {
        CHAT,
        JOIN,
        TYPING,
        ROOM
    }
}

