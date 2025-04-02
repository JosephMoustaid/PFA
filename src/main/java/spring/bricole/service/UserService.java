package spring.bricole.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final ConversationService conversationService;

    public UserService(ConversationService conversationService) {
        this.conversationService = conversationService;
    }


    // delete a user , you need to delete all the conversations of that user
    public void deleteUser(int id){

    }
}
