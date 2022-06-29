package org.trading.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.function.Consumer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.trading.SystemProperties;
import org.trading.command.ClosePositionCommand;
import org.trading.command.Command;
import org.trading.fsm.InitialState;
import org.trading.fsm.SystemState;
import org.trading.model.IndicatorState;
import org.trading.model.MarketInfo;
import org.trading.model.OrderHandler;

/**
 * Expected is that once the system starts it waits for the first ATR update,
 * once it has that it tries to place an order. Once the order is placed it updates to order on
 * each ATR update with correct stop/target and position size.
 */
@Slf4j
@Data
public class SystemData {
  private SystemProperties systemProperties = new SystemProperties();
  @JsonIgnore
  private SystemState state = new InitialState();
  private final String epic;
  private final MarketInfo marketInfo;
  private final OpeningRange openingRange;
  private final OrderHandler orderHandler = new OrderHandler();
  @JsonIgnore
  private final Consumer<Command> commandExecutor;
  private MidPriceEvent currentMidPrice;
  private final IndicatorEvent currentIndicatorEvent;
   // Used to initialize system with an account equity at creation, then it will be updated from outside.
  public AccountEquityEvent currentAccountEquity; // IS initalized when we create SystemData so never null

  public void setState(SystemState newState) {
    log.info("{} -> {} For {}", state.getClass().getSimpleName(), newState.getClass().getSimpleName(), epic);
    this.state = newState;
  }

  public SystemData(String epic, MarketInfo marketInfo, OpeningRange openingRange,
      Consumer<Command> commandExecutor, IndicatorState indicatorState) {
    this.epic = epic;
    this.marketInfo = marketInfo;
    this.openingRange = openingRange;
    this.commandExecutor = commandExecutor;
    this.currentIndicatorEvent = new IndicatorEvent(epic, indicatorState);
  }

  public void handleMidPriceEvent(MidPriceEvent event) {
    if (currentMidPrice == null) {
      currentMidPrice = event;
    } else {
      currentMidPrice.setLevel(event.getLevel());
      currentMidPrice.setSpread(event.getSpread());
    }
    if (orderHandler.hasPosition()) {
      orderHandler.getPosition().updateMae(event);
    }
    state.handleMidPriceEvent(this, event);
  }
  public void handleIndicatorEvent(IndicatorEvent event) {
    if (orderHandler.hasPosition()) {
      orderHandler.getPosition().incrementBarsSinceEntry();
    }
    currentIndicatorEvent.setIndicatorState(event.getIndicatorState());
    state.handleAtrEvent(this, event);
  }

  public void handleOpuEvent(Opu event) {
    state.handleOpuEvent(this, event);
  }

  public void handleConfirmsEvent(Confirms event) {
    state.handleConfirmsEvent(this, event);
  }

  public void closeMarket() {
    if (orderHandler.hasPosition()) {
      commandExecutor.accept(ClosePositionCommand.from(epic, marketInfo.getExpiry(), orderHandler.getPosition().getOrder().getSize(), orderHandler.getPosition().getOrder().getDirection()));
    }
  }

}
