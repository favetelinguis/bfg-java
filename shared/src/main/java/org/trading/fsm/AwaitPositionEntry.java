package org.trading.fsm;

import org.trading.command.DeleteWorkingOrderCommand;
import org.trading.event.AtrEvent;
import org.trading.event.Confirms;
import org.trading.event.MarketClose;
import org.trading.event.MidPriceEvent;
import org.trading.event.Opu;
import org.trading.event.SystemData;

public class AwaitPositionEntry implements SystemState {

  @Override
  public void handleMidPriceEvent(SystemData s, MidPriceEvent event) {

  }

  @Override
  public void handleAtrEvent(SystemData s, AtrEvent event) {
    if (s.getCurrentMidPrice().isOutsideBuffer(s.getOpeningRange(), s.getCurrentAtr())) {
      for (var dealId : s.getOrderHandler().getDealIds()) {
        s.getCommandExecutor().accept(DeleteWorkingOrderCommand.from(s.getEpic(), dealId));
      }
      s.setState(new AwaitDeleteWorkingOrder());
    }
  }

  @Override
  public void handleOpuEvent(SystemData s, Opu event) {
    if (event.isPositionEntry()) {
      s.getOrderHandler().getOtherDealId(event).ifPresent(dealId ->
          s.getCommandExecutor().accept(DeleteWorkingOrderCommand.from(s.getEpic(), dealId)));
      s.getOrderHandler().createPosition(event);
      s.setState(new AwaitPositionExit());
    }
  }

  @Override
  public void handleConfirmsEvent(SystemData s, Confirms event) {

  }

}
