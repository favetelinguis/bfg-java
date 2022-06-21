package org.trading.model;

import lombok.Data;
import org.trading.event.AtrEvent;

@Data
public class Order {
  private final String direction;
  private final AtrEvent currentAtr;
  private final Double wantedEntryPrice;
  private String dealId;

  public Order(String direction, Double wantedEntryLevel, AtrEvent currentAtr) {
    this.direction = direction;
    this.currentAtr = currentAtr;
    this.wantedEntryPrice = wantedEntryLevel;
  }
}
