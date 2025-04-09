package spring.bricole.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring.bricole.repository.NotificationRepository;

@Service
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
}
