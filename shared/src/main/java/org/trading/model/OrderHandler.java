package org.trading.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.trading.command.CreateWorkingOrderCommand;
import org.trading.event.Opu;

@Slf4j
public class OrderHandler {
  private Order buy;
  private Order sell;
  @Getter
  private Position position;

  public OrderHandler(Order order1, Order order2) {
    assert !order1.getDirection().equals(order2.getDirection());
    if (order1.getDirection().equals("BUY")) {
      this.buy = order1;
      this.sell = order2;
    } else if (order2.getDirection().equals("BUY")){
      this.buy = order2;
      this.sell = order1;
    } else {
      throw new IllegalArgumentException("One must be a BUY order");
    }
  }

  public OrderHandler(Order order) {
    if (order.getDirection().equals("BUY")) {
      this.buy = order;
    } else if (order.getDirection().equals("SELL")){
      this.sell = order;
    } else {
      throw new IllegalArgumentException("Order must be either buy or sell");
    }
  }

  public void createPosition(Opu event) {
    if (event.getDirection().equals("BUY")) {
      position = new Position(buy, event.getLevel());
    } else if (event.getDirection().equals("SELL")) {
      position = new Position(sell, event.getLevel());
    } else {
      log.error("Invalid direction in OPU event {}", event);
    }
  }

  public boolean isPositionCreated() {
    return position != null && position.getState().equals("CREATED");
  }

  public boolean hasOrder() {
    return (buy != null || sell != null) && position == null;
  }
}
