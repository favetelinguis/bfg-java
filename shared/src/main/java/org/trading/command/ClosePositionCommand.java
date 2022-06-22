package org.trading.command;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClosePositionCommand implements Command {

  private final String expiry;
  private final String epic;
  private final Double size;

  public static ClosePositionCommand from(String epic, String expiry, Double size) {
    return new ClosePositionCommand(epic, expiry, size);
  }
}
