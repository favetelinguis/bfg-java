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
  public boolean isInside(OpeningRange openingRange, AtrEvent atr) {
      if (openingRange != null && atr != null) {
        return (level > (openingRange.getMidLow() + (bufferMultipleOfAtr * atr.getAtr())))  && (level < (openingRange.getMidHigh() - (bufferMultipleOfAtr * atr.getAtr())));
      }
      return false;
  }

}
