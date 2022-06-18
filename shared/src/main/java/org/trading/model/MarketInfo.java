package org.trading.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import lombok.Data;

// TODO add validation to this class so when i read props it get validated
@Data
public class MarketInfo {
  String epic;
  String expiry;
  String currency;
  Double minStop;
  Double maxStopMultiplier;
  Double lotSize;
  LocalTime localOpenTime;
  LocalTime localCloseTime;
  String marketZone;
  Set<LocalDate> nonTradingDays;
  Long barsInOpeningRange;

  public boolean isEu() {
    return marketZone.equals("EU");
  }

  public boolean isUs() {
    return marketZone.equals("US");
  }
}
