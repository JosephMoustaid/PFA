package spring.bricole.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name="conversation")
public class Conversation {
    // == fields ==
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", referencedColumnName = "id")
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", referencedColumnName = "id")
    private User user2;

    @Column(name="last_message")
    private String lastMessage;

    @Column(name="last_message_at")
    private LocalDateTime lastMessageAt;


    @OneToMany(mappedBy = "conversation", fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();

    // == Constructor ==
    public Conversation(User user1, User user2, String lastMessage, LocalDateTime lastMessageAt) {
        this.user1 = user1;
        this.user2 = user2;
        this.lastMessage = lastMessage;
        this.lastMessageAt = lastMessageAt;
    }

    // == Methods ==
    public void addMessage(Message message) {
        messages.add(message);
    }

}
