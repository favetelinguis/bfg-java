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
@Role(Type.EVENT)
@Duration("duration")
@Timestamp("startTime")
@Expires("1h30m")
public class Candle {
  private Date startTime;
  private Long duration;
}
