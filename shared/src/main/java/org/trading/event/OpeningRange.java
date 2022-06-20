package org.trading.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;
import org.trading.SystemProperties;

/**
 * Shows the opening range as defined by the market, could be 1min opening range or any
 * multiple of that. midHigh and midLow are calculated as (bid + ask) / 2
 */
@AllArgsConstructor
@Data
@Role(Type.EVENT)
@Expires("9h")
public class OpeningRange extends SystemProperties {
  String epic;
  Double midHigh;
  Double midLow;

  public Double pipsInOpeningRange() {
    return midHigh - midLow;
  }

  public boolean isLargeEnough(AtrEvent atr) {
    return pipsInOpeningRange() >= (atr.getLevel() * minAtrMultipleForOpeningRange);
  }
}
