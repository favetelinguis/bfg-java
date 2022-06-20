package org.trading.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;
import org.trading.SystemProperties;

@AllArgsConstructor
@Data
@Role(Type.EVENT)
@Expires("5s")
public class MidPriceEvent extends SystemProperties {
  String epic;
  Double level;
  Double spread;

  public boolean isOver(OpeningRange openingRange, AtrEvent atr) {
    if (openingRange != null && atr != null) {
      return level > (openingRange.getMidHigh() + (bufferMultipleOfAtr * atr.getLevel()));
    }
    return false;
  }
  public boolean isUnder(OpeningRange openingRange, AtrEvent atr) {
    if (openingRange != null && atr != null) {
      return level < (openingRange.getMidLow() - (bufferMultipleOfAtr * atr.getLevel()));
    }
    return false;
  }
  public boolean isInside(OpeningRange openingRange, AtrEvent atr) {
      if (openingRange != null && atr != null) {
        return !isOver(openingRange, atr) && !isUnder(openingRange, atr);
      }
      return false;
  }
}
