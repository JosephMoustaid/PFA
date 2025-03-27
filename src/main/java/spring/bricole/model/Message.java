package spring.bricole.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GeneratedColumn;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name="message")
public class Message {

    // == Fields ==
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String content;
    @Column(name="sent_at")
    private LocalDateTime sendAt;
    private boolean isRead;
    private boolean isSent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private User sender;

    // == Constructor ==
    public Message(String content, LocalDateTime sendAt, boolean isRead, boolean isSent, User sender) {
        this.content = content;
        this.sendAt = sendAt;
        this.isRead = isRead;
        this.isSent = isSent;
        this.sender = sender;
    }
}
