package org.trading.fsm;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.trading.event.IndicatorEvent;
import org.trading.event.Confirms;
import org.trading.event.MidPriceEvent;
import org.trading.event.Opu;
import org.trading.event.SystemData;

@Slf4j
@Data
public class AwaitPositionTrailingStopConfirmation implements SystemState {

  @Override
  public void handleMidPriceEvent(SystemData s, MidPriceEvent event) {

  }

  @Override
  public void handleAtrEvent(SystemData s, IndicatorEvent event) {

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
