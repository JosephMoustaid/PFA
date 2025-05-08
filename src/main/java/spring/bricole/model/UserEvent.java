package spring.bricole.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import spring.bricole.common.EventType;
import spring.bricole.common.Role;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "user_events")
public class UserEvent {
    @Id
    private String id;
    private int userId;
    private EventType eventType;
    private LocalDateTime timestamp = LocalDateTime.now();
    private Map<String, Object> metadata;
    private Role role;

}

