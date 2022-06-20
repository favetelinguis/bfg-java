package org.trading.command;

import java.time.Instant;
import lombok.Data;

@Data
public class TradeResultCommand implements Command {
  private Double size;
  private Double wantedEntryLevel;
  private Double actualEntryLevel;
  private Instant entryTime;
  private Instant exitTime;
  private Double wantedExitLevel;
  private Double actualExitLevel;
  private String direction;
  private String entryLocationInOpeningRange;
  private String epic;
  private Integer version;
  private Double oneR;
}
