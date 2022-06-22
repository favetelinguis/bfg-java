package org.trading.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.trading.market.data.BarUpdate;

@Component
public class WebsocketEventListener {

  private final SimpMessageSendingOperations messagingTemplate;

  @Autowired
  public WebsocketEventListener(SimpMessageSendingOperations messagingTemplate) {

    this.messagingTemplate = messagingTemplate;
  }

  @EventListener(BarUpdate.class)
  public void handle(BarUpdate event) {
    messagingTemplate.convertAndSend("/topic/greetings", new Greeting(event.getEpic()));
  }
}
