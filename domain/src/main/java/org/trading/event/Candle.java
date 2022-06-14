package org.trading.event;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;
import org.kie.api.definition.type.Timestamp;

@Data
@AllArgsConstructor
public class Candle {
  private String epic;
}
