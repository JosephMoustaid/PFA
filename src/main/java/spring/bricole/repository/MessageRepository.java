package spring.bricole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spring.bricole.model.Message;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    // No need to add any methods here
    // cause Conversation will handle all the data retrieval methods


    /*
    @Query("INSERT INTO Message m (m.conversation_id, m.is_read , m.is_sent, m.sender, m.sendAt, m.receiver, m.content) " +
            "VALUES (:conversationId, :isRead, :isSent, :sender, :sendAt, :receiver, :content)")
    void sendMessage(int conversationId, boolean isRead, boolean isSent, int sender, Timestamp timestamp, int receiver, String content);
*/

}
