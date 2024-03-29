package org.trading.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.trading.SystemProperties;

@Data
public class MidPriceEvent {
  private String epic;
  private Double level;
  private Double spread;
  @JsonIgnore
  private SystemProperties systemProperties = new SystemProperties();

  public MidPriceEvent(String epic, Double level, Double spread) {
    this.epic = epic;
    this.level = level;
    this.spread = spread;
  }

  public boolean isOver(OpeningRange openingRange, IndicatorEvent atr) {
    return isOver(openingRange, atr, 1.);
  }
  public boolean isUnder(OpeningRange openingRange, IndicatorEvent atr) {
    return isUnder(openingRange, atr, 1.);
  }
  // Opening Range mut be large enough and price must be inside
  public boolean isInside(OpeningRange openingRange, IndicatorEvent atr) {
    return isInside(openingRange, atr, 1.);
  }

  public boolean isOver(OpeningRange openingRange, IndicatorEvent event, Double bufferMultiplier) {
    if (openingRange != null && event != null) {
      return level > (openingRange.getMidHigh() + (bufferMultiplier * systemProperties.bufferMultipleOfAtr * event.getIndicatorState().getAtr()));
    }
    return false;
  }
  public boolean isUnder(OpeningRange openingRange, IndicatorEvent event, Double bufferMultiplier) {
    if (openingRange != null && event != null) {
      return level < (openingRange.getMidLow() - (bufferMultiplier * systemProperties.bufferMultipleOfAtr * event.getIndicatorState().getAtr()));
    }
    return false;
  }
  // Opening Range mut be large enough and price must be inside
  public boolean isInside(OpeningRange openingRange, IndicatorEvent event, Double bufferMultiplier) {
    if (openingRange != null && event != null) {
      var priceIsInside = (level > (openingRange.getMidLow() + (bufferMultiplier*systemProperties.bufferMultipleOfAtr * event.getIndicatorState().getAtr())))  && (level < (openingRange.getMidHigh() - (systemProperties.bufferMultipleOfAtr * event.getIndicatorState().getAtr())));
      return priceIsInside && openingRange.isLargeEnough(event);
    }
    return false;
  }


  /**
   * This function makes use of buffer multiplier to make sure we are outside buffer with some margin.
   * The reason is that we use this function to update an order with new stop and target, we dont want
   * to make this update when we are to close to the buffer since then the time it takes from we close an order
   * and then find entry need to find an entry outside the buffer but the time it takes make cause the market to move into the buffer
   * which would result in find entry not be able to reopen position. Its better to keep the old atr then miss a trade.
   * @param openingRange
   * @param currentAtr
   * @return
   */
  public boolean isWellOutsideBuffer(OpeningRange openingRange, IndicatorEvent currentAtr) {
    return isOver(openingRange, currentAtr, 1.5) || isInside(openingRange, currentAtr, 1.5) || isUnder(openingRange, currentAtr, 1.5);
  }
}
