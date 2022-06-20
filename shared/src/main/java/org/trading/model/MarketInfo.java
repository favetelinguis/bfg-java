package org.trading.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import lombok.Data;

// TODO add validation to this class so when i read props it get validated
@Data
public class MarketInfo {
  private String epic;
  private String expiry;
  private String currency;
  private Double minStop;
  private Double maxStopMultiplier;
  private Double lotSize;
  private LocalTime localOpenTime;
  private LocalTime localCloseTime;
  private String marketZone;
  private Set<LocalDate> nonTradingDays;
  private Long barsInOpeningRange;
  private Double valueOfOnePip;

  public boolean isEu() {
    return marketZone.equals("EU");
  }

  public boolean isUs() {
    return marketZone.equals("US");
  }
}
