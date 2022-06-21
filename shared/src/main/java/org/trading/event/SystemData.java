package org.trading.event;

import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;
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

/**
 * Expected is that once the system starts it waits for the first ATR update,
 * once it has that it tries to place an order. Once the order is placed it updates to order on
 * each ATR update with correct stop/target and position size.
 */
@Slf4j
public class SystemData extends SystemProperties {

  @Getter
  private final String epic;
  private final MarketInfo marketInfo;
  private final OpeningRange openingRange;
  private final OrderHandler orderHandler = new OrderHandler();
  private final Consumer<Command> commandExecutor;
  private MidPriceEvent currentMidPrice;
  public AtrEvent currentAtr;
  @Setter
  public AccountEquityEvent currentAccountEquity;


  public SystemData(String epic, MarketInfo marketInfo, OpeningRange openingRange,
      Consumer<Command> commandExecutor) {
    this.epic = epic;
    this.marketInfo = marketInfo;
    this.openingRange = openingRange;
    this.commandExecutor = commandExecutor;
  }

  /**
   * For each event, if there is no open order or position check if we can open one.
   */
  public void updateMidPrice(MidPriceEvent event) {
    this.currentMidPrice = event;
    if (orderHandler.noOrder()) {
      if (currentMidPrice.isOver(openingRange, currentAtr)) {
        var wantedEntryLevel = openingRange.getWantedEntryLevel("SELL_HIGH", currentMidPrice);
        orderHandler.setOrder(new Order("BUY", wantedEntryLevel, currentAtr));
        orderHandler.getBuy().ifPresent(order ->
            commandExecutor.accept(CreateWorkingOrderCommand.from(
                    order.getDirection(),
                    marketInfo,
                    getTodayMarketClose(marketInfo),
                    order.getCurrentAtr().targetDistance(),
                    order.getCurrentAtr().stopDistance(),
                    order.getCurrentAtr().positionSize(marketInfo, currentAccountEquity),
                    order.getWantedEntryPrice()
                )
            )
        );
      } else if (currentMidPrice.isInside(openingRange, currentAtr)) {
        var wantedEntryLevelBuy = openingRange.getWantedEntryLevel("BUY_LOW", currentMidPrice);
        var wantedEntryLevelSell = openingRange.getWantedEntryLevel("SELL_HIGH", currentMidPrice);
        orderHandler.setOrder(new Order("BUY", wantedEntryLevelBuy, currentAtr));
        orderHandler.setOrder(new Order("SELL", wantedEntryLevelSell, currentAtr));
        orderHandler.getBuy().ifPresent(order ->
            commandExecutor.accept(CreateWorkingOrderCommand.from(
                    order.getDirection(),
                    marketInfo,
                    getTodayMarketClose(marketInfo),
                    order.getCurrentAtr().targetDistance(),
                    order.getCurrentAtr().stopDistance(),
                    order.getCurrentAtr().positionSize(marketInfo, currentAccountEquity),
                    order.getWantedEntryPrice()
                )
            )
        );
        orderHandler.getSell().ifPresent(order ->
            commandExecutor.accept(CreateWorkingOrderCommand.from(
                order.getDirection(),
                marketInfo,
                getTodayMarketClose(marketInfo),
                order.getCurrentAtr().targetDistance(),
                order.getCurrentAtr().stopDistance(),
                order.getCurrentAtr().positionSize(marketInfo, currentAccountEquity),
                order.getWantedEntryPrice()
            )));
      } else if (currentMidPrice.isUnder(openingRange, currentAtr)) {
        var wantedEntryLevel = openingRange.getWantedEntryLevel("SELL_LOW", currentMidPrice);
        orderHandler.setOrder(new Order("SELL", wantedEntryLevel, currentAtr));
        orderHandler.getSell().ifPresent(order ->
            commandExecutor.accept(CreateWorkingOrderCommand.from(
                order.getDirection(),
                marketInfo,
                getTodayMarketClose(marketInfo),
                order.getCurrentAtr().targetDistance(),
                order.getCurrentAtr().stopDistance(),
                order.getCurrentAtr().positionSize(marketInfo, currentAccountEquity),
                order.getWantedEntryPrice()
            )));
      }
    } else if (orderHandler.isPositionCreated()) {
      if (orderHandler.getPosition().get().isInProfit(currentMidPrice)) {
        commandExecutor.accept(UpdatePositionCommand.from(epic, orderHandler.getPosition().get()));
        orderHandler.getPosition().get().setState("TRY_TRAIL");
      }
    }
  }

  /**
   * Important that we stop updating ATR once we have a position since we want to use the last know
   * order adjustment when calculating the levels for adding tailing stop.
   */
  public void updateAtr(AtrEvent event) {
    if (!orderHandler.hasPosition()) {
      try {
        if (currentAtr == null) {
          currentAtr = event;
        } else {
          this.currentAtr.setAtr(event.getAtr()); // Stop updating ATR once we have a position
        }
      } catch (Exception e) {
        log.error("Failed setting ATR", e);
      }
      log.info("SYSTEM ATR {}", currentAtr.getAtr());
      var marketClose = getTodayMarketClose(marketInfo);
      orderHandler.getSell()
          .filter(order -> order.getDealId() != null) // make sure to only do this if orders are accepted else I will get 404 error since the is no dealid
          .ifPresent(order -> commandExecutor.accept(
              UpdateWorkingOrderCommand.from(epic, order, marketInfo, currentAccountEquity,
                  marketClose)));
      orderHandler.getBuy()
          .filter(order -> order.getDealId() != null) // make sure to only do this if orders are accepted else I will get 404 error since the is no dealid
          .ifPresent(order -> commandExecutor.accept(
              UpdateWorkingOrderCommand.from(epic, order, marketInfo, currentAccountEquity,
                  marketClose)));
    }
  }

  public void updateOrderStatus(Opu event) {
      if (event.isPositionEntry()) {
        log.info("Position opened for {}", epic);
        orderHandler.createPosition(event);
        commandExecutor.accept(
            DeleteWorkingOrderCommand.from(epic, orderHandler.getOtherDealId(event)));
      } else if (event.isPositionExit()) {
        log.info("Position exit for {}", epic);
        commandExecutor.accept(new TradeResultCommand());
        orderHandler.setBuy(null);
        orderHandler.setSell(null);
        orderHandler.setPosition(null);
      }
  }

  public void updateOrderStatus(Confirms event) {
      if (event.isRejected()) {
        log.info("Order rejected {}", event);
      } else if (event.isOrderCreatedSuccess()) {
        orderHandler.setDealId(event);
        log.info("Order create success {}", event.getEpic());
      } else if (event.isOrderUpdatedSuccess()) {
        log.info("Order update success {}", event.getEpic());
      } else if (event.isOrderDeletedSuccess()) {
        log.info("Order deleted success {}", event.getEpic());
      }
  }

  public void updateOrderStatus(MarketClose event) {
    log.warn("Market close so close all order and positions");
  }

  private Double getEntryLevel(String direction, Double level) {
    if (direction.equals("BUY")) {
      return level + currentMidPrice.getSpread();
    } else {
      return level - currentMidPrice.getSpread();
    }
  }
}
