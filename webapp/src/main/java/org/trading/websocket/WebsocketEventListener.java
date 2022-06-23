package org.trading.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.trading.event.MidPriceEvent;
import org.trading.market.data.BarUpdate;

@Component
public class WebsocketEventListener {

  private final SimpMessageSendingOperations messagingTemplate;

  @Autowired
  public WebsocketEventListener(SimpMessageSendingOperations messagingTemplate) {

    this.messagingTemplate = messagingTemplate;
  }

  @EventListener(MidPriceEvent.class)
  public void handle(MidPriceEvent event) {
    messagingTemplate.convertAndSend("/topic/" + event.getEpic() + "/midPrice", event);
  }
}
