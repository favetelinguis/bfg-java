package org.trading.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.trading.event.MidPriceEvent;

@Data
public class Position {
  private Order order;
  private Double entryPrice;

  public Position(Order order, Double entryPrice) {
    this.order = order;
    this.entryPrice = entryPrice;
  }

  // TODO price will be midPrice while entry price will be BID or ASK will that be an issue?
  public boolean isInProfit(MidPriceEvent price) {
    if (order.getDirection().equals("SELL")) {
      return price.getLevel() < entryPrice;
    } else {
      return price.getLevel() > entryPrice;
    }
  }

  public Double getStopLevel() {
    if (order.getDirection().equals("BUY")) {
      return entryPrice - order.getCurrentAtr().stopDistance();
    } else {
      return entryPrice + order.getCurrentAtr().stopDistance();
    }
  }
  public Double getTargetLevel() {
    if (order.getDirection().equals("BUY")) {
      return entryPrice + order.getCurrentAtr().targetDistance();
    } else {
      return entryPrice - order.getCurrentAtr().targetDistance();
    }
  }

  public Double getTrailingStopDistance() {
      return order.getCurrentAtr().stopDistance();
  }
}
