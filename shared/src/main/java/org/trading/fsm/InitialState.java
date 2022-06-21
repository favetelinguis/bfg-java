package org.trading.fsm;

import org.trading.event.AtrEvent;
import org.trading.event.Confirms;
import org.trading.event.MarketClose;
import org.trading.event.MidPriceEvent;
import org.trading.event.Opu;
import org.trading.event.SystemData;

public class InitialState implements SystemState {

  @Override
  public void handleMidPriceEvent(SystemData s, MidPriceEvent event) {

  }

  @Override
  public void handleAtrEvent(SystemData s, AtrEvent event) {
    s.setState(new FindEntry());
  }

  @Override
  public void handleOpuEvent(SystemData s, Opu event) {

  }

  @Override
  public void handleConfirmsEvent(SystemData s, Confirms event) {

  }

  @Override
  public void handleMarketClose(SystemData s, MarketClose event) {

  }
}
