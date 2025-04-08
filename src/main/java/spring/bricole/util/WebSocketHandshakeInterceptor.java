package spring.bricole.util;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import spring.bricole.util.JwtUtil;

import java.util.Map;

@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            var httpRequest = servletRequest.getServletRequest();





            // Try to get token from Authorization header
            String token = null;
            String authHeader = httpRequest.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            } else {
                // Fallback to query param ?token=
                String paramToken = httpRequest.getParameter("token");
                if (paramToken != null) {
                    // Add "Bearer " prefix if missing
                    attributes.put("Authorization", paramToken.startsWith("Bearer ") ? paramToken : "Bearer " + paramToken);
                } else {
                    throw new IllegalArgumentException("Missing or invalid token");
                }
            }

            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("Missing or invalid token");
            }

            JwtUtil.TokenValidationResult result = JwtUtil.validateToken(token);
            if (result == null) {
                throw new IllegalArgumentException("Invalid token");
            }

            // ✅ Store user info in attributes for WebSocket session
            attributes.put("userId", result.userId());
            attributes.put("token", token);
        }

        return true;
    }


    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // No-op
    }
}

