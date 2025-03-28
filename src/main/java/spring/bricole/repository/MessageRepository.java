package spring.bricole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.bricole.model.Message;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    /*
    Data retrieval methods:

     */
}
