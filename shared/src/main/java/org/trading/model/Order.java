package org.trading.model;

import lombok.Data;

@Data
public class Order {
  private final String direction;
  private final Double wantedEntryPrice;
  private Double size;
  private Double stopDistance;
  private Double targetDistance;
  private String entryType;
  private String dealId;

  public Order(String direction, Double wantedEntryLevel, Double size, Double targetDistance, Double stopDistance, String entryType) {
    this.direction = direction;
    this.wantedEntryPrice = wantedEntryLevel;
    this.size = size;
    this.stopDistance = stopDistance;
    this.targetDistance = targetDistance;
    this.entryType = entryType;
  }
}
