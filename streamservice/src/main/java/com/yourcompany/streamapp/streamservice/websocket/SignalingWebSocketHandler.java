package com.yourcompany.streamapp.streamservice.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourcompany.streamapp.streamservice.dto.WebSocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SignalingWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(SignalingWebSocketHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Map<StreamID, Set<WebSocketSession>>
    private final Map<Long, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long streamId = getStreamId(session);
        logger.info("Session {} connected for stream {}", session.getId(), streamId);
        rooms.computeIfAbsent(streamId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long streamId = getStreamId(session);
        WebSocketMessage wsMessage = objectMapper.readValue(message.getPayload(), WebSocketMessage.class);
        logger.info("Message received for stream {}: {}", streamId, message.getPayload());

        // Broadcast the message to all other participants in the same room
        broadcast(streamId, session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long streamId = getStreamId(session);
        logger.info("Session {} disconnected from stream {}", session.getId(), streamId);
        Set<WebSocketSession> sessions = rooms.get(streamId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                rooms.remove(streamId);
            }
        }
    }

    private void broadcast(Long streamId, WebSocketSession sender, TextMessage message) {
        Set<WebSocketSession> sessions = rooms.get(streamId);
        if (sessions == null) return;

        for (WebSocketSession session : sessions) {
            // Send to everyone except the original sender
            if (session.isOpen() && !session.getId().equals(sender.getId())) {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    logger.error("Error broadcasting message to session {}: {}", session.getId(), e.getMessage());
                }
            }
        }
    }

    private Long getStreamId(WebSocketSession session) {
        // Extract streamId from the WebSocket URI, e.g., /ws/signal/{streamId}
        String path = session.getUri().getPath();
        return Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
    }
}