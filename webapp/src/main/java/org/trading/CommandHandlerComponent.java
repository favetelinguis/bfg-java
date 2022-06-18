package org.trading;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.trading.command.ClosePositionCommand;
import org.trading.command.CreateWorkingOrderCommand;
import org.trading.command.DeleteWorkingOrderCommand;
import org.trading.command.TradeResultCommand;
import org.trading.command.UpdateWorkingOrderCommand;
import org.trading.ig.IgRestService;
import org.trading.repository.IgStreamRepository;
import org.trading.repository.TradeResultEntity;
import org.trading.repository.TradeResultRepository;

@Slf4j
@Component
public class CommandHandlerComponent {

  private final IgRestService igRestService;
  private final TradeResultRepository tradeResultRepository;

  @Autowired
  public CommandHandlerComponent(IgRestService igRestService, TradeResultRepository tradeResultRepository) {
    this.igRestService = igRestService;
    this.tradeResultRepository = tradeResultRepository;
  }
  @Async
  @EventListener(CreateWorkingOrderCommand.class)
  public void createWorkingOrder(CreateWorkingOrderCommand command) {
    igRestService.createOrder();
    log.info("Create working order for epic {}", command.getEpic());
  }
  @Async
  @EventListener(DeleteWorkingOrderCommand.class)
  public void deleteWorkingOrder(DeleteWorkingOrderCommand command) {
    igRestService.deleteOrder("dummy");
    log.info("Delete working order for epic {}", command.getEpic());
  }
  @Async
  @EventListener(UpdateWorkingOrderCommand.class)
  public void updateWorkingOrder(UpdateWorkingOrderCommand command) {
    igRestService.updateOrder("dummy");
    log.info("Update working order for epic {}", command.getEpic());
  }
  @Async
  @EventListener(ClosePositionCommand.class)
  public void closePosition(ClosePositionCommand command) {
    igRestService.closePosition();
    log.info("Close position for epic {}", command.getEpic());
  }
  @Async
  @EventListener(TradeResultCommand.class)
  public void tradeResults(TradeResultCommand command) {
    tradeResultRepository.save(new TradeResultEntity(LocalDateTime.now(), command));
  }
}
