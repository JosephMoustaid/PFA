package spring.bricole.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spring.bricole.model.Message;
import spring.bricole.model.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    //Data retrieval methods:

    // get all notifications of the reciever user by id
    List<Message> findByReceiverIdOrderByCreatedAt(int receiverId);

}
