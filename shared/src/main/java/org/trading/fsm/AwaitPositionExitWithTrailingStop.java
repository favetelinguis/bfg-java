package org.trading.fsm;

import java.time.Instant;
import lombok.Data;
import org.trading.command.TradeResultCommand;
import org.trading.event.IndicatorEvent;
import org.trading.event.Confirms;
import org.trading.event.MidPriceEvent;
import org.trading.event.Opu;
import org.trading.event.SystemData;

@Data
public class AwaitPositionExitWithTrailingStop implements SystemState {

  @Override
  public void handleMidPriceEvent(SystemData s, MidPriceEvent event) {
  }

  @Override
  public void handleAtrEvent(SystemData s, IndicatorEvent event) {

  }

  @Override
  public void handleOpuEvent(SystemData s, Opu event) {
    if (event.isPositionExit()) {
      s.setState(new FindEntry());
      s.getOrderHandler().getPosition().setUtcExit(Instant.now());
      s.getOrderHandler().getPosition().setActualExitPrice(event.getLevel());
      s.getCommandExecutor().accept(TradeResultCommand.from(s.getOrderHandler().getPosition()));
      s.getOrderHandler().resetOrders();
    }
  }

  @Override
  public void handleConfirmsEvent(SystemData s, Confirms event) {

  }

}
