package spring.bricole.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter @AllArgsConstructor @NoArgsConstructor
public class CreateConversationRequestDTO {
    private int id; // conversation id
    private int user1Id; // user1 id
    private int user2Id;    // user2 id
}
