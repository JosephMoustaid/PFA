package spring.bricole.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.*;
import spring.bricole.util.AuthHandshakeInterceptor;
import spring.bricole.util.WebSocketHandshakeInterceptor;

import spring.bricole.util.JwtUtil;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new WebSocketHandshakeInterceptor());
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                    //  token validation
                    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                        String token = accessor.getFirstNativeHeader("Authorization");
                        System.out.println("Received Authorization Header: " + token);  // Debugging log

                        if (token != null && token.startsWith("Bearer ")) {
                            token = token.substring(7);  // Strip the "Bearer " part
                            JwtUtil.TokenValidationResult result = JwtUtil.validateToken(token);
                            if (result == null) throw new IllegalArgumentException("Invalid token");

                            // Optionally attach user ID to the session
                            accessor.setUser(() -> String.valueOf(result.userId()));
                        } else {
                            throw new IllegalArgumentException("Missing or invalid Authorization header");
                        }

                    }
                return message;
            }
        });
    }
}




