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
import org.trading.command.UpdatePositionCommand;
import org.trading.command.UpdateWorkingOrderCommand;
import org.trading.ig.IgRestService;
import org.trading.repository.CommandEntity;
import org.trading.repository.CommandRepository;
import org.trading.repository.IgStreamRepository;
import org.trading.repository.TradeResultEntity;
import org.trading.repository.TradeResultRepository;

@Slf4j
@Component
public class CommandHandlerComponent {

  private final IgRestService igRestService;
  private final TradeResultRepository tradeResultRepository;
  private final CommandRepository commandRepository;

  @Autowired
  public CommandHandlerComponent(IgRestService igRestService, TradeResultRepository tradeResultRepository, CommandRepository commandRepository) {
    this.igRestService = igRestService;
    this.tradeResultRepository = tradeResultRepository;
    this.commandRepository = commandRepository;
  }
  @Async
  @EventListener(CreateWorkingOrderCommand.class)
  public void createWorkingOrder(CreateWorkingOrderCommand command) {
    commandRepository.save(new CommandEntity("CreateWorkingOrderCommand", command));
    igRestService.createOrder(command);
    log.info("Create working order for epic {}", command.getEpic());
  }
  @Async
  @EventListener(DeleteWorkingOrderCommand.class)
  public void deleteWorkingOrder(DeleteWorkingOrderCommand command) {
    commandRepository.save(new CommandEntity("DeleteWorkingOrderCommand", command));
    igRestService.deleteOrder(command.getDealId());
    log.info("Delete working order for epic {}", command.getEpic());
  }
  @Async
  @EventListener(UpdateWorkingOrderCommand.class)
  public void updateWorkingOrder(UpdateWorkingOrderCommand command) {
    commandRepository.save(new CommandEntity("UpdateWorkingOrderCommand", command));
    igRestService.updateOrder(command);
    log.info("Update working order for epic {}", command.getEpic());
  }
  @Async
  @EventListener(UpdatePositionCommand.class)
  public void updatePosition(UpdatePositionCommand command) {
    commandRepository.save(new CommandEntity("UpdatePositionCommand", command));
    igRestService.updatePosition(command);
    log.info("Update position order for epic {}", command.getEpic());
  }
  @Async
  @EventListener(ClosePositionCommand.class)
  public void closePosition(ClosePositionCommand command) {
    commandRepository.save(new CommandEntity("ClosePositionCommand", command));
    igRestService.closePosition();
    log.info("Close position for epic {}", command.getEpic());
  }
  @Async
  @EventListener(TradeResultCommand.class)
  public void tradeResults(TradeResultCommand command) {
    tradeResultRepository.save(new TradeResultEntity(LocalDateTime.now(), command));
  }
}
