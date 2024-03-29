package org.trading.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import lombok.Data;

// TODO add validation to this class so when i read props it get validated
@Data
public class MarketInfo {
  private String epic;
  private String expiry;
  private String currency;
  private Double minStop;
  private Double lotSize;
  private LocalTime localOpenTime;
  private LocalTime localCloseTime;
  private Set<LocalDate> nonTradingDays;
  private Long barsInOpeningRange;
  private Double valueOfOnePip; // TODO this is not good now since this is a rogh estimate when my account is in SEK but 1pip is in EUR so i give this in sek
  private Double marginRequirement;
}
