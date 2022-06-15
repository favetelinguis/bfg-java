package org.trading.event;

import java.time.Instant;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;
import org.kie.api.definition.type.Timestamp;

@Data
public class Candle {
  private final String epic;
  private Double askOpen;
  private Double askHigh;
  private Double askLow;
  private Double askClose;
  private Double bidOpen;
  private Double bidHigh;
  private Double bidLow;
  private Double bidClose;
  private Long numberTicks;
  private Instant updateTime;

  public Candle(String epic) {
    this.epic = epic;
  }
}
