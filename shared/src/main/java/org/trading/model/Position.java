package org.trading.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.trading.event.MidPriceEvent;

@Data
public class Position {
  private Order order;
  private Double entryPrice;
  private Double entryStropDistance;
  private Double entryTargetDistance;

  public Position(Order order, Double entryPrice) {
    this.order = order;
    this.entryPrice = entryPrice;
    this.entryStropDistance = order.getStopDistance();
    this.entryTargetDistance = order.getTargetDistance();
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
      return entryPrice - entryStropDistance;
    } else {
      return entryPrice + entryStropDistance;
    }
  }
  public Double getTargetLevel() {
    if (order.getDirection().equals("BUY")) {
      return entryPrice + entryTargetDistance;
    } else {
      return entryPrice - entryTargetDistance;
    }
  }

  public Double getTrailingStopDistance() {
      return entryStropDistance;
  }
}
