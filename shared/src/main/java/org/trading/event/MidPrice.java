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
public class MidPrice extends SystemProperties {
  String epic;
  Double level;
  Double spread;

  public boolean isOver(OpeningRange openingRange, Atr atr) {
    return level > (openingRange.getMidHigh() + (bufferMultipleOfAtr * atr.getLevel()));

  }
  public boolean isUnder(OpeningRange openingRange, Atr atr) {
    return level < (openingRange.getMidLow() - (bufferMultipleOfAtr * atr.getLevel()));
  }
  public boolean isInside(OpeningRange openingRange, Atr atr) {
    return !isOver(openingRange, atr) && !isUnder(openingRange, atr);
  }
}