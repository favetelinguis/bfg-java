package org.trading.market;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Opening time is the actual time the market opens without and delta.
 */
@AllArgsConstructor
@Data
public class MarketOpen {
  String epic;
  LocalTime openingTime;
  Long barsInOpeningRange;
}
