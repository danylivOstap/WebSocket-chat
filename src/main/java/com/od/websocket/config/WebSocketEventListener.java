package com.od.websocket.config;

import com.od.websocket.chat.ChatMessage;
import com.od.websocket.chat.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
  private final SimpMessageSendingOperations messagingTemplate;

  @EventListener
  public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
    final String username = (String) headerAccessor.getSessionAttributes().get("username");

    if (username != null) {
      log.info("user disconnected: {}", username);
      final ChatMessage message = ChatMessage.builder()
          .type(MessageType.LEAVE)
          .sender(username)
          .build();
      messagingTemplate.convertAndSend("/topic/public", message);
    }
  }
}
