package spring.bricole.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "notification")
public class Notification {
    // == Fields ==
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private User sender;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recieverId", referencedColumnName = "id")
    private User receiver;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // == Constructors ==
    public Notification(String message, User sender, User receiver, LocalDateTime createdAt) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.createdAt = createdAt;
    }
}