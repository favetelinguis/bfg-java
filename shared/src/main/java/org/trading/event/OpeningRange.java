package org.trading.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.trading.SystemProperties;

/**
 * Shows the opening range as defined by the market, could be 1min opening range or any
 * multiple of that. midHigh and midLow are calculated as (bid + ask) / 2
 */
@Data
public class OpeningRange {
  private String epic;
  private Double midHigh;
  private Double midLow;
  @JsonIgnore
  private SystemProperties systemProperties = new SystemProperties();

  public OpeningRange(String epic, Double midHigh, Double midLow) {
    this.epic = epic;
    this.midHigh = midHigh;
    this.midLow = midLow;
  }

  public Double pipsInOpeningRange() {
    return midHigh - midLow;
  }

  public boolean isLargeEnough(AtrEvent atr) {
    return pipsInOpeningRange() >= (atr.getAtr() * systemProperties.minAtrMultipleForOpeningRange);
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
