package org.trading.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.trading.SystemProperties;

/**
 * Shows the opening range as defined by the market, could be 1min opening range or any
 * multiple of that. midHigh and midLow are calculated as (bid + ask) / 2
 */
@AllArgsConstructor
@Data
public class OpeningRange extends SystemProperties {
  private String epic;
  private Double midHigh;
  private Double midLow;

  public Double pipsInOpeningRange() {
    return midHigh - midLow;
  }

  public boolean isLargeEnough(AtrEvent atr) {
    return pipsInOpeningRange() >= (atr.getAtr() * minAtrMultipleForOpeningRange);
  }
  public Double getWantedEntryLevel(String entryType, MidPriceEvent midPrice) {
    if (entryType.equals("BUY_LOW")) {
      return midLow + midPrice.getSpread();
    } else if (entryType.equals("BUY_HIGH")) {
      return midHigh + midPrice.getSpread();
    } else if (entryType.equals("SELL_LOW")) {
      return midLow - midPrice.getSpread();
    } else if (entryType.equals("SELL_HIGH")) {
      return midHigh - midPrice.getSpread();
    }
    throw new IllegalArgumentException("unknown entry type");
  }
}
