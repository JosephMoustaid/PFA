package spring.bricole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.bricole.model.Conversation;
import spring.bricole.model.Employer;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {

    //Data retrieval methods:

    //get the conversation of 2 users by their ids
    Optional<Conversation> findByUser1IdAndUser2Id(int user1Id, int user2Id);

}
