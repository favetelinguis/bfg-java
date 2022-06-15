package org.trading.event;

import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;

/**
 * Opening time is the actual time the market opens without and delta.
 */
@AllArgsConstructor
@Data
@Role(Type.EVENT)
public class MarketOpen {
  String epic;
  LocalTime openingTime;
  Long barsInOpeningRange;
}
