package spring.bricole;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import spring.bricole.model.*;
import spring.bricole.repository.*;
import spring.bricole.util.DummyDataGenerator;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class BricoleApplication {

    public static void main(String[] args) {
        SpringApplication.run(BricoleApplication.class, args);
    }

    /*
    @Bean
    CommandLineRunner initDatabase(
            AdminRepository adminRepository,
            UserRepository userRepository,
            EmployeeRepository employeeRepository,
            EmployerRepository employerRepository,
            JobRepository jobRepository,
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            NotificationRepository notificationRepository,
            ReviewRepository reviewRepository) {

        return args -> {
            // Generate dummy data
            List<Object> dummyData = DummyDataGenerator.generateDummyData();

            // First save all users (including employees and employers)
            for (Object entity : dummyData) {
                if (entity instanceof User) {
                    userRepository.save((User) entity);
                }
            }

            // Then save all admins
            for (Object entity : dummyData) {
                if (entity instanceof Admin) {
                    adminRepository.save((Admin) entity);
                }
            }

            // Then save all jobs (which reference employers)
            for (Object entity : dummyData) {
                if (entity instanceof Job) {
                    jobRepository.save((Job) entity);
                }
            }

            // Save conversations first (without messages)
            for (Object entity : dummyData) {
                if (entity instanceof Conversation) {
                    Conversation conversation = (Conversation) entity;
                    // Clear messages temporarily to avoid persistence issues
                    List<Message> messages = conversation.getMessages();
                    conversation.setMessages(new ArrayList<>());
                    conversationRepository.save(conversation);

                    // Save messages with the conversation_id reference
                    for (Message message : messages) {
                        messageRepository.save(message);
                    }

                    // Update conversation with messages (if needed for further operations)
                    conversation.setMessages(messages);
                }
            }

            // Save notifications
            for (Object entity : dummyData) {
                if (entity instanceof Notification) {
                    notificationRepository.save((Notification) entity);
                }
            }

            // Save reviews
            for (Object entity : dummyData) {
                if (entity instanceof Review) {
                    reviewRepository.save((Review) entity);
                }
            }

            System.out.println("Database populated with dummy data!");
        };

    }
    */
}