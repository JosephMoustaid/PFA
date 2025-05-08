package spring.bricole.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.bricole.common.EventType;
import spring.bricole.common.Role;
import spring.bricole.model.UserEvent;
import spring.bricole.repository.UserEventRepository;

import java.util.List;
import java.util.Map;

@Service
public class EventLoggingService {

    @Autowired
    private UserEventRepository eventRepository;

    public void log(int userId, Role role, EventType eventType, Map<String, Object> metadata) {
        UserEvent event = new UserEvent();
        event.setUserId(userId);
        event.setEventType(eventType);
        event.setRole(role);
        event.setMetadata(metadata);
        eventRepository.save(event);
    }

}
