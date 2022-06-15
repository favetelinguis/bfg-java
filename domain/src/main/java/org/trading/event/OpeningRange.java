package org.trading.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;

/**
 * Shows the opening range as defined by the market, could be 1min opening range or any
 * multiple of that. midHigh and midLow are calculated as (bid + ask) / 2
 */
@AllArgsConstructor
@Data
@Role(Type.EVENT)
public class OpeningRange {
  String epic;
  Double midHigh;
  Double midLow;
}
