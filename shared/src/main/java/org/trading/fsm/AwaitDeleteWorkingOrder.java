package org.trading.fsm;

import org.trading.command.DeleteWorkingOrderCommand;
import org.trading.event.AtrEvent;
import org.trading.event.Confirms;
import org.trading.event.MarketClose;
import org.trading.event.MidPriceEvent;
import org.trading.event.Opu;
import org.trading.event.SystemData;

public class AwaitDeleteWorkingOrder implements SystemState {

  private int numConfirms = 0;
  private boolean hasRejection = false;
  @Override
  public void handleMidPriceEvent(SystemData s, MidPriceEvent event) {

  }

  @Override
  public void handleAtrEvent(SystemData s, AtrEvent event) {

  }

  @Override
  public void handleOpuEvent(SystemData s, Opu event) {

  }

  @Override
  public void handleConfirmsEvent(SystemData s, Confirms event) {
    numConfirms++;
    if (s.getOrderHandler().hasBuyAndSellOrder()) { // TODO probably bug here when I have buy and sell and only one get rejected I should only wait for one delete but now i expect two
      if (numConfirms == 2) {
        s.getOrderHandler().resetOrders();
        s.setState(new FindEntry());
      }
    } else {
      s.getOrderHandler().resetOrders();
      s.setState(new FindEntry());
    }
  }

  @Override
  public void handleMarketClose(SystemData s, MarketClose event) {

  }
}
