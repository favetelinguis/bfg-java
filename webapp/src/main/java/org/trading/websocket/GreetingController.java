package org.trading.websocket;

import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.HtmlUtils;
import org.trading.brain.BrainComponent;
import org.trading.event.SystemData;

@Controller
public class GreetingController {

  private final BrainComponent brain;

  @Autowired
  public GreetingController(BrainComponent brain) {
    this.brain = brain;
  }

  @MessageMapping("/system")
  @SendTo("/topic/system")
  public Collection<SystemData> greeting() {
    return brain.getActiveSystems();
  }
}
