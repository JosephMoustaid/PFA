package spring.bricole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.bricole.model.Conversation;
import spring.bricole.model.Employer;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {

    //Data retrieval methods:

    //get the conversation of 2 users by their ids
    Optional<Conversation> findByUser1IdAndUser2Id(int user1Id, int user2Id);


    //get all conversations of a user by their id
    List<Conversation> findALlByUser1_Id(int userId);
    List<Conversation> findALlByUser2_Id(int userId);
}
