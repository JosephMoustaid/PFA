package spring.bricole.service;

import org.springframework.stereotype.Service;
import spring.bricole.common.AccountStatus;
import spring.bricole.model.User;
import spring.bricole.repository.UserRepository;

@Service
public class UserService {

    private final ConversationService conversationService;
    private final UserRepository userRepository;
    public UserService(ConversationService conversationService,  UserRepository userRepository ) {
        this.conversationService = conversationService;
        this.userRepository = userRepository;
    }


    public void updateUserAccountStatus(int id , AccountStatus status){
        int updatedCount = userRepository.updateStatusById(id, status);
        if (updatedCount == 0) {
            throw new RuntimeException("No user found with id: " + id);
        }
    }
    // delete a user , you need to delete all the conversations of that user
    public void deleteUser(int id){

    }

    public User getUserById(int id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
