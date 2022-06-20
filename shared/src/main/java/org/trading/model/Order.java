package org.trading.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Order {
  private String direction;
  private String dealId;
  private Double wantedEntryPrice;

  public Order(String direction) {
    this.direction = direction;
  }
}
