package spring.bricole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.bricole.model.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    // No need to add any methods here
    // cause Conversation will handle all the data retrieval methods

}
