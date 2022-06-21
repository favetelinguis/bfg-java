package org.trading.model;

import java.util.Optional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.trading.event.Confirms;
import org.trading.event.Opu;

@Slf4j
@Data
public class OrderHandler {
  private Order buy;
  private Order sell;
  private Position position;

  public void setOrder(Order order) {
    if (order.getDirection().equals("BUY")) {
      this.buy = order;
    } else if (order.getDirection().equals("SELL")){
      this.sell = order;
    } else {
      throw new IllegalArgumentException("Order must be either buy or sell");
    }
  }

  public Optional<Order> getBuy() {
    return Optional.ofNullable(buy);
  }

  public Optional<Order> getSell() {
    return Optional.ofNullable(sell);
  }

  public Optional<Position> getPosition() {
    return Optional.ofNullable(position);
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
    return Optional.ofNullable(position).filter(p -> p.getState().equals("CREATED")).isPresent();
  }

  public boolean noOrder() {
    return buy == null && sell == null;
  }

  public boolean hasPosition() {
    return position != null;
  }

  public void setDealId(Confirms event) {
    if (event.getDirection().equals("BUY")) {
      buy.setDealId(event.getDealId());
    } else {
      sell.setDealId(event.getDealId());
    }
  }

  public String getOtherDealId(Opu event) {
    if (event.getDirection().equals("BUY")) {
      return sell.getDealId(); // TODO this can be null?
    } else {
      return buy.getDealId();
    }
  }
}
