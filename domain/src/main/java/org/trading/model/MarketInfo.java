package org.trading.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.Data;

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
  List<LocalDate> nonTradingDays;
  Long barsInOpeningRange;
}
