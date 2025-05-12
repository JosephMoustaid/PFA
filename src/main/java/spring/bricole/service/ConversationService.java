package spring.bricole.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.bricole.common.EventType;
import spring.bricole.common.Role;
import spring.bricole.model.Conversation;
import spring.bricole.repository.ConversationRepository;

import java.util.List;
import java.util.Map;

import spring.bricole.model.User;
import spring.bricole.repository.UserRepository;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private UserRepository userRepository;
    private final EventLoggingService eventLoggingService;
    public ConversationService(ConversationRepository conversationRepository,
                               EventLoggingService eventLoggingService) {
        this.conversationRepository = conversationRepository;
        this.eventLoggingService = eventLoggingService;
    }

    // get total number of conversations
    public long getTotalConversations() {
        return conversationRepository.count();
    }
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Conversation getConversationById(int conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
    }

    public Conversation getConversationByUserIds(int user1Id, int user2Id) {
        return conversationRepository.findByUser1IdAndUser2Id(user1Id, user2Id)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
    }

    public Conversation createConversation(Conversation conversation) {
        return conversationRepository.save(conversation);
    }

    public void updateConversation(Conversation conversation) {
        conversationRepository.save(conversation);
    }

    public List<Conversation> getAllConversationsByUserId(int userId) {
        List<Conversation> conversations = conversationRepository.findALlByUser1_Id(userId);
        conversations.addAll(conversationRepository.findALlByUser2_Id(userId));
        return conversations;
    }

    public Conversation createNewConversation(int user1Id, int user2Id) {
        Conversation conversation = new Conversation();

        User user1 = userRepository.findById(user1Id).orElseThrow(() -> new RuntimeException("User not found"));
        User user2 = userRepository.findById(user2Id).orElseThrow(() -> new RuntimeException("User not found"));

        conversation.setUser1(user1);
        conversation.setUser2(user2);
        conversation.setLastMessage("");
        conversation.setLastMessageAt(null);

        eventLoggingService.log(conversation.getUser1().getId(), Role.USER, EventType.NEW_CONVERSATION , Map.of("user1Id", user1Id
                , "user2Id", user2Id));
        return conversationRepository.save(conversation);
    }
}