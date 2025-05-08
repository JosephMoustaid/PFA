package spring.bricole.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring.bricole.repository.NotificationRepository;

@Service
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    // get total number of ntifs
    public long getTotalNotifications() {
        return notificationRepository.count();
    }
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
}
