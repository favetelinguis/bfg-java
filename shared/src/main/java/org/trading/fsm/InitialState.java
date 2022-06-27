package org.trading.fsm;

import lombok.Data;
import org.trading.event.IndicatorEvent;
import org.trading.event.Confirms;
import org.trading.event.MidPriceEvent;
import org.trading.event.Opu;
import org.trading.event.SystemData;

@Data
public class InitialState implements SystemState {

  @Override
  public void handleMidPriceEvent(SystemData s, MidPriceEvent event) {
    s.setState(new FindEntry());
  }

  @Override
  public void handleAtrEvent(SystemData s, IndicatorEvent event) {
  }

  @Override
  public void handleOpuEvent(SystemData s, Opu event) {

  }

  @Override
  public void handleConfirmsEvent(SystemData s, Confirms event) {

  }

}
