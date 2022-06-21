package org.trading.fsm;

import org.trading.command.TradeResultCommand;
import org.trading.command.UpdatePositionCommand;
import org.trading.event.AtrEvent;
import org.trading.event.Confirms;
import org.trading.event.MarketClose;
import org.trading.event.MidPriceEvent;
import org.trading.event.Opu;
import org.trading.event.SystemData;

public class AwaitPositionExit implements SystemState {

  @Override
  public void handleMidPriceEvent(SystemData s, MidPriceEvent event) {
    if (s.getOrderHandler().getPosition().isInProfit(s.getCurrentMidPrice())) {
      s.getCommandExecutor().accept(UpdatePositionCommand.from(s.getEpic(), s.getOrderHandler().getPosition()));
      s.setState(new AwaitPositionTrailingStopConfirmation());
    }
  }

  @Override
  public void handleAtrEvent(SystemData s, AtrEvent event) {

  }

  @Override
  public void handleOpuEvent(SystemData s, Opu event) {
    if (event.isPositionExit()) {
      s.getCommandExecutor().accept(TradeResultCommand.from(s.getOrderHandler().getPosition()));
      s.getOrderHandler().resetOrders();
      s.setState(new FindEntry());
    }
  }

  @Override
  public void handleConfirmsEvent(SystemData s, Confirms event) {

  }

  @Override
  public void handleMarketClose(SystemData s, MarketClose event) {

  }
}
