package org.trading.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Position {
  String epic;
  Order order;
  Double entryPrice;
  String state;

  public Position(Order order, Double entryPrice) {
    this.order = order;
    this.entryPrice = entryPrice;
    this.epic = order.getEpic();
    this.state = "CREATED";
  }

  // TODO price will be midPrice while entry price will be BID or ASK will that be an issue?
  public boolean isInProfit(Double price) {
    if (order.direction.equals("SELL")) {
      return price < entryPrice;
    } else {
      return price > entryPrice;
    }
  }
}
