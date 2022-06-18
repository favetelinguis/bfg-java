package org.trading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.trading.command.ClosePositionCommand;
import org.trading.command.CreateWorkingOrderCommand;
import org.trading.command.DeleteWorkingOrderCommand;
import org.trading.command.UpdateWorkingOrderCommand;

@Component
public class CommandHandlerComponent {
  private static Logger LOG = LoggerFactory.getLogger(CommandHandlerComponent.class);

  @Async
  @EventListener(CreateWorkingOrderCommand.class)
  public void createWorkingOrder(CreateWorkingOrderCommand command) {
    LOG.info("Create working order for epic {}", command.getEpic());
  }
  @Async
  @EventListener(DeleteWorkingOrderCommand.class)
  public void deleteWorkingOrder(DeleteWorkingOrderCommand command) {
    LOG.info("Delete working order for epic {}", command.getEpic());
  }
  @Async
  @EventListener(UpdateWorkingOrderCommand.class)
  public void updateWorkingOrder(UpdateWorkingOrderCommand command) {
    LOG.info("Update working order for epic {}", command.getEpic());
  }
  @Async
  @EventListener(ClosePositionCommand.class)
  public void closePosition(ClosePositionCommand command) {
    LOG.info("Close position for epic {}", command.getEpic());
  }
}
