package spring.bricole.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import spring.bricole.common.EventType;
import spring.bricole.common.Role;
import spring.bricole.model.UserEvent;

import java.util.List;

public interface UserEventRepository extends MongoRepository<UserEvent, String> {
    /*
    Get the full list of login events for all users
    get the full list of login events for all admins
    get the full list of login events for all employees
    get the full list of login events for all employers

    get latest 100 login event for all users

    get latest 100 signup event for all users
     */

    List<UserEvent> findByEventTypeOrderByTimestampDesc(String eventType);

    List<UserEvent> findByEventTypeAndRole(EventType eventType, Role role);

    List<UserEvent> findTop100ByEventTypeOrderByTimestampDesc(EventType eventType);

    List<UserEvent> findTop100ByOrderByTimestampDesc();
}