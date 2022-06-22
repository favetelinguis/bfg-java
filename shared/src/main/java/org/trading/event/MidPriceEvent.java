package org.trading.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.trading.SystemProperties;

@AllArgsConstructor
@Data
public class MidPriceEvent extends SystemProperties {
  String epic;
  Double level;
  Double spread;

  public boolean isOver(OpeningRange openingRange, AtrEvent atr) {
    if (openingRange != null && atr != null) {
      return level > (openingRange.getMidHigh() + (bufferMultipleOfAtr * atr.getAtr()));
    }
    return false;
  }
  public boolean isUnder(OpeningRange openingRange, AtrEvent atr) {
    if (openingRange != null && atr != null) {
      return level < (openingRange.getMidLow() - (bufferMultipleOfAtr * atr.getAtr()));
    }
    return false;
  }
  // Opening Range mut be large enough and price must be inside
  public boolean isInside(OpeningRange openingRange, AtrEvent atr) {
      if (openingRange != null && atr != null) {
        var priceIsInside = (level > (openingRange.getMidLow() + (bufferMultipleOfAtr * atr.getAtr())))  && (level < (openingRange.getMidHigh() - (bufferMultipleOfAtr * atr.getAtr())));
        return priceIsInside && openingRange.isLargeEnough(atr);
      }
      return false;
  }

  public boolean isOutsideBuffer(OpeningRange openingRange, AtrEvent currentAtr) {
    return isOver(openingRange, currentAtr) || isInside(openingRange, currentAtr) || isUnder(openingRange, currentAtr);
  }
}
