package spring.bricole.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.bricole.dto.CreateConversationRequestDTO;
import spring.bricole.model.Conversation;
import spring.bricole.service.ConversationService;
import spring.bricole.service.MessageService;
import spring.bricole.util.JwtUtil;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    MessageService messageService ;
    ConversationService conversationService;

    public ChatRestController(MessageService messageService, ConversationService conversationService) {
        this.messageService = messageService;
        this.conversationService = conversationService;
    }


    // Helper method to extract user ID from JWT token
    private int extractUserIdFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid or missing Authorization header");
        }
        String token = authorizationHeader.substring(7); // Remove "Bearer "


        JwtUtil.TokenValidationResult validation = JwtUtil.validateToken(token);
        return validation.userId(); // Returns the authenticated user's ID
    }


    @GetMapping("/ping")
    public String ping() {
        return "Chat WebSocket API is running.";
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<Conversation>> getUserConversations(@RequestHeader("Authorization") String auth) {
        int userId = extractUserIdFromToken(auth);
        return ResponseEntity.ok(conversationService.getAllConversationsByUserId(userId));
    }

    // start a new conversation
    @PostMapping("/conversation/new")
    public ResponseEntity<Conversation> startNewConversation(
            @RequestHeader("Authorization") String auth,
            @RequestBody CreateConversationRequestDTO conversation) {
        int userId = extractUserIdFromToken(auth);

        conversation.setUser1Id(userId);
        conversation.setUser2Id(conversation.getUser2Id());

        return ResponseEntity.ok(conversationService.createNewConversation(
                conversation.getUser1Id(),
                conversation.getUser2Id()
        ));
    }
    @GetMapping("/conversation/{id}")
    public ResponseEntity<Conversation> getConversationById(
            @RequestHeader("Authorization") String auth,
            @PathVariable int id) {
        int userId = extractUserIdFromToken(auth);
        Conversation conversation = conversationService.getConversationById(id);
        if (conversation.getUser1().getId() != userId && conversation.getUser2().getId() != userId) {
            throw new RuntimeException("You are not authorized to view this conversation");
        }
        return ResponseEntity.ok(conversation);
    }
    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
}
