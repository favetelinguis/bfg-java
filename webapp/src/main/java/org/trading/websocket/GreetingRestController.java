package org.trading.websocket;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.trading.brain.BrainComponent;
import org.trading.event.SystemData;

@RestController
public class GreetingRestController {

  private final BrainComponent brain;

  @Autowired
  public GreetingRestController(BrainComponent brain) {
    this.brain = brain;
  }

  @GetMapping("/api/system")
  public Collection<SystemData> greeting() {
    return brain.getActiveSystems();
  }
}
