package org.trading.fsm;

import lombok.Data;
import org.trading.command.DeleteWorkingOrderCommand;
import org.trading.event.IndicatorEvent;
import org.trading.event.Confirms;
import org.trading.event.MidPriceEvent;
import org.trading.event.Opu;
import org.trading.event.SystemData;

@Data
public class AwaitCreateWorkingOrder implements SystemState {
  private int numConfirms = 0;
  private boolean hasRejection = false;
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
    numConfirms++;
    if (event.isRejected()) {
      hasRejection = true;
    } else {
      s.getOrderHandler().setDealId(event); // Dont set dealId on rejected orders
    }
    if (s.getOrderHandler().hasBuyAndSellOrder()) {
      if (numConfirms == 2) {
        if (hasRejection) { // One or both WO orders has been rejected
          var dealIds = s.getOrderHandler().getDealIds();
          if (dealIds.size() > 0) {
            for (var dealId: dealIds) {
              s.getCommandExecutor().accept(DeleteWorkingOrderCommand.from(s.getEpic(), dealId));
            }
            s.setState(new AwaitDeleteWorkingOrder());
          } else { // Both are rejected so nothing to delete
            s.getOrderHandler().resetOrders();
            s.setState(new FindEntry());
          }
        } else {
          s.setState(new AwaitPositionEntry());
        }
      }
    } else {
      if (hasRejection) { // We only have one buy or sell order it got rejected so try again
        s.getOrderHandler().resetOrders();
        s.setState(new FindEntry());
      } else {
        s.setState(new AwaitPositionEntry());
      }
    }
  }
}
