package org.trading.command;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClosePositionCommand implements Command {
  private final String epic;
  private final String expiry;
  private final Double size;
  private final String direction;

  public static ClosePositionCommand from(String epic, String expiry, Double size, String direction) {
    if (direction.equals("BUY")) {
      return new ClosePositionCommand(epic, expiry, size, "SELL");
    }
    return new ClosePositionCommand(epic, expiry, size, "BUY");
  }
}
