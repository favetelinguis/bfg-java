package org.trading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.trading.command.ClosePositionCommand;

@Component
public class CommandHandlerComponent {
  private static Logger LOG = LoggerFactory.getLogger(CommandHandlerComponent.class);

  @EventListener(ClosePositionCommand.class)
  public void closePosition(ClosePositionCommand command) {
    LOG.info("ATR in CODE!!! - am i ever alone?");
  }
}
