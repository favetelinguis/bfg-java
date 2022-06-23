package org.trading.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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

  public void createPosition(Opu event) {
    if (event.getDirection().equals("BUY")) {
      position = new Position(buy, event.getLevel(), Instant.now());
    } else if (event.getDirection().equals("SELL")) {
      position = new Position(sell, event.getLevel(), Instant.now());
    } else {
      log.error("Invalid direction in OPU event {}", event);
    }
  }

  public boolean hasBuyOrder() {
    return buy != null;
  }

  public boolean hasSellOrder() {
    return sell != null;
  }

  public boolean hasBuyAndSellOrder() {
    return hasBuyOrder() && hasSellOrder();
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

  public Optional<String> getOtherDealId(Opu event) {
    if (event.getDirection().equals("BUY")) {
      if (sell != null) {
        return Optional.of(sell.getDealId());
      } else {
        return Optional.empty();
      }
    } else {
      if (buy != null) {
        return Optional.of(buy.getDealId());
      } else Optional.empty();
    }
    return Optional.empty();
  }

  public void resetOrders() {
    buy = null;
    sell = null;
    position = null;
  }

  public List<String>  getDealIds() {
    List<String> dealIds = new ArrayList<>();
    if (hasSellOrder()) {
      var dealId = sell.getDealId();
      if (dealId != null) {
        dealIds.add(dealId);
      }
    }
    if (hasBuyOrder()) {
      var dealId = buy.getDealId();
      if (dealId != null) {
        dealIds.add(dealId);
      }
    }
    return dealIds;
  }
}
