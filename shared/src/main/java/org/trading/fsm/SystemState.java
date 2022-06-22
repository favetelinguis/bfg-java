package org.trading.fsm;

import org.trading.event.AtrEvent;
import org.trading.event.Confirms;
import org.trading.event.MarketClose;
import org.trading.event.MidPriceEvent;
import org.trading.event.Opu;
import org.trading.event.SystemData;

public interface SystemState {
  void handleMidPriceEvent(SystemData s, MidPriceEvent event);
  void handleAtrEvent(SystemData s, AtrEvent event);
  void handleOpuEvent(SystemData s, Opu event);
  void handleConfirmsEvent(SystemData s, Confirms event);
}
