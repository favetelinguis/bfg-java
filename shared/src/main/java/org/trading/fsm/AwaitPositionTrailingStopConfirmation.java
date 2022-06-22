package org.trading.fsm;

import org.trading.event.AtrEvent;
import org.trading.event.Confirms;
import org.trading.event.MarketClose;
import org.trading.event.MidPriceEvent;
import org.trading.event.Opu;
import org.trading.event.SystemData;

public class AwaitPositionTrailingStopConfirmation implements SystemState {

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
    if (event.isOrderUpdatedSuccess()) {
      s.setState(new AwaitPositionExitWithTrailingStop());
    }
  }

}