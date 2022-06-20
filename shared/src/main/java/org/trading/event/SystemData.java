package org.trading.event;

import java.util.function.Consumer;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.trading.SystemProperties;
import org.trading.command.Command;
import org.trading.command.CreateWorkingOrderCommand;
import org.trading.command.DeleteWorkingOrderCommand;
import org.trading.command.TradeResultCommand;
import org.trading.command.UpdatePositionCommand;
import org.trading.command.UpdateWorkingOrderCommand;
import org.trading.model.MarketInfo;
import org.trading.model.Order;
import org.trading.model.OrderHandler;
import org.trading.model.Position;

@Slf4j
public class SystemData extends SystemProperties {
  @Getter
  private final String epic;
  private final MarketInfo marketInfo;
  private final OpeningRange openingRange;
  private OrderHandler orderHandler;
  private final Consumer<Command> commandExecutor;
  private MidPriceEvent currentMidPrice;
  public AtrEvent currentAtr;
  public AccountEquityEvent currentAccountEquity;


  public SystemData(String epic, MarketInfo marketInfo, OpeningRange openingRange, Consumer<Command> commandExecutor) {
    this.epic = epic;
    this.marketInfo = marketInfo;
    this.openingRange = openingRange;
    this.commandExecutor = commandExecutor;
  }

  /**
   * For each event, if there is no open order or position check if we can open one.
   * @param event
   */
  public void updateMidPrice(MidPriceEvent event) {
    this.currentMidPrice = event;
    if (orderHandler == null) {
      if (currentMidPrice.isOver(openingRange, currentAtr)) {
        commandExecutor.accept(new CreateWorkingOrderCommand(epic));
        orderHandler = new OrderHandler(new Order("BUY"));
      } else if (currentMidPrice.isInside(openingRange, currentAtr)) {
        commandExecutor.accept(new CreateWorkingOrderCommand(epic));
        commandExecutor.accept(new CreateWorkingOrderCommand(epic));
        orderHandler = new OrderHandler(new Order("BUY"), new Order("SELL"));
      } else if (currentMidPrice.isUnder(openingRange, currentAtr)) {
        commandExecutor.accept(new CreateWorkingOrderCommand(epic));
        orderHandler = new OrderHandler(new Order("SELL"));
      }
    } else if (orderHandler.isPositionCreated()) {
      if (orderHandler.getPosition().isInProfit(currentMidPrice)) {
        commandExecutor.accept(new UpdatePositionCommand(epic));
        orderHandler.getPosition().setState("TRY_TRAIL");
      }
    }
  }

  public void updateAtr(AtrEvent event) {
    this.currentAtr = event;
    if (orderHandler != null && orderHandler.hasOrder()) {
      commandExecutor.accept(new UpdateWorkingOrderCommand(epic));
    }
  }

  public void updateOrderStatus(Opu event) {
    if (orderHandler != null) {
      if (event.isPositionEntry()) {
        orderHandler.createPosition(event);
        commandExecutor.accept(new DeleteWorkingOrderCommand(epic));
      } else if (event.isPositionExit()) {
        commandExecutor.accept(new TradeResultCommand());
      } else {
        log.error("Unknown OPU event {}", event);
      }
    } else {
      log.error("Order handler is null for opu event");
    }
  }

  public void updateOrderStatus(Confirms event) {
    if (orderHandler != null) {
      if (event.isOrderCreatedRejected()) {
        log.info("Order create rejected {}", event.getEpic());
      } else if (event.isOrderCreatedSuccess()) {
        log.info("Order create success {}", event.getEpic());
      } else if (event.isOrderUpdatedRejected()) {
        log.info("Order update rejected {}", event.getEpic());
      } else if (event.isOrderUpdatedSuccess()) {
        log.info("Order update success {}", event.getEpic());
      } else if (event.isOrderDeletedRejected()) {
        log.info("Order deleted rejected {}", event.getEpic());
      } else if (event.isOrderDeletedSuccess()) {
        log.info("Order deleted success {}", event.getEpic());
      }
    } else {
      log.error("Order handler is null for confirms event");
    }
  }

  public void updateOrderStatus(MarketClose event) {
    log.warn("Market close so close all order and positions");
  }
}
