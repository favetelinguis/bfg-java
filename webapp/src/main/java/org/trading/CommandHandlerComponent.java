package org.trading;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.trading.command.ClosePositionCommand;
import org.trading.command.CreateWorkingOrderCommand;
import org.trading.command.DeleteWorkingOrderCommand;
import org.trading.command.UpdateWorkingOrderCommand;

@Slf4j
@Component
public class CommandHandlerComponent {
  @Async
  @EventListener(CreateWorkingOrderCommand.class)
  public void createWorkingOrder(CreateWorkingOrderCommand command) {
    log.info("Create working order for epic {}", command.getEpic());
  }
  @Async
  @EventListener(DeleteWorkingOrderCommand.class)
  public void deleteWorkingOrder(DeleteWorkingOrderCommand command) {
    log.info("Delete working order for epic {}", command.getEpic());
  }
  @Async
  @EventListener(UpdateWorkingOrderCommand.class)
  public void updateWorkingOrder(UpdateWorkingOrderCommand command) {
    log.info("Update working order for epic {}", command.getEpic());
  }
  @Async
  @EventListener(ClosePositionCommand.class)
  public void closePosition(ClosePositionCommand command) {
    log.info("Close position for epic {}", command.getEpic());
  }
}
