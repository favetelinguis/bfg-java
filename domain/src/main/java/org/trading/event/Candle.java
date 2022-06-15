package org.trading.event;

import java.time.Instant;
import lombok.Data;

@Data
public class Candle {
  private final String epic;
  private Double askOpen;
  private Double askHigh;
  private Double askLow;
  private Double askClose;
  private Double bidOpen;
  private Double bidHigh;
  private Double bidLow;
  private Double bidClose;
  private Long numberTicks;
  private Instant updateTime;

  public Candle(String epic) {
    this.epic = epic;
  }
}
