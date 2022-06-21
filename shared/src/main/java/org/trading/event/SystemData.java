package org.trading.event;

import java.util.function.Consumer;
import lombok.Data;
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
import org.trading.fsm.InitialState;
import org.trading.fsm.SystemState;
import org.trading.model.MarketInfo;
import org.trading.model.Order;
import org.trading.model.OrderHandler;

/**
 * Expected is that once the system starts it waits for the first ATR update,
 * once it has that it tries to place an order. Once the order is placed it updates to order on
 * each ATR update with correct stop/target and position size.
 */
@Slf4j
@Data
public class SystemData extends SystemProperties {
  private SystemState state = new InitialState();
  private final String epic;
  private final MarketInfo marketInfo;
  private final OpeningRange openingRange;
  private final OrderHandler orderHandler = new OrderHandler();
  private final Consumer<Command> commandExecutor;
  private MidPriceEvent currentMidPrice;
  private AtrEvent currentAtr;
   // Used to initialize system with an account equity at creation, then it will be updated from outside.
  public AccountEquityEvent currentAccountEquity;

  public SystemData(String epic, MarketInfo marketInfo, OpeningRange openingRange,
      Consumer<Command> commandExecutor) {
    this.epic = epic;
    this.marketInfo = marketInfo;
    this.openingRange = openingRange;
    this.commandExecutor = commandExecutor;
  }

  public void handleMidPriceEvent(MidPriceEvent event) {
    if (currentMidPrice == null) {
      currentMidPrice = event;
    } else {
      currentMidPrice.setLevel(event.getLevel());
      currentMidPrice.setSpread(event.getSpread());
    }
    state.handleMidPriceEvent(this, event);
  }
  /**
   * Important that we stop updating ATR once we have a position since we want to use the last know
   * order adjustment when calculating the levels for adding tailing stop.
   */
  public void handleAtrEvent(AtrEvent event) { // TODO need to fix the comment I dont want o stop updating atr need some other way to find levels when adding trail
    if (currentAtr == null) {
      currentAtr = event;
    } else {
      currentAtr.setAtr(event.getAtr());
    }
    state.handleAtrEvent(this, event);
  }

  public void handleOpuEvent(Opu event) {
    state.handleOpuEvent(this, event);
  }

  public void handleConfirmsEvent(Confirms event) {
    state.handleConfirmsEvent(this, event);
  }

  public void handleMarketClose(MarketClose event) {
    state.handleMarketClose(this, event);
  }
}
