package org.trading.command;

import java.time.Instant;
import lombok.Data;
import org.trading.model.Position;

@Data
public class TradeResultCommand implements Command {
  private String epic;
  private Double size;
  private Double wantedEntryLevel;
  private Double actualEntryLevel;
  private Instant entryTime;
  private Instant exitTime;
  private Double wantedExitLevel;
  private Double actualExitLevel;
  private String direction;
  private String entryType;
  private Integer version;
  private Double oneR;
  private Integer barsInTrade;

  public static TradeResultCommand from(Position position) {
    var command = new TradeResultCommand();
    command.setEpic(command.getEpic());
    command.setSize(position.getOrder().getSize());
    command.setWantedEntryLevel(position.getOrder().getWantedEntryPrice());
    command.setActualEntryLevel(position.getEntryPrice());
    command.setEntryTime(position.getUtcEntry());
    command.setExitTime(position.getUtcExit());
    command.setWantedExitLevel(position.getWantedStopLevel());
    command.setActualExitLevel(position.getActualExitPrice());
    command.setDirection(position.getOrder().getDirection());
    command.setOneR(position.getOrder().getStopDistance());
    command.setEntryType(position.getOrder().getEntryType());
    command.setBarsInTrade(position.getBarsSinceEntry());
    return command;
  }

  public Double calculateRPnL() {
    Double result;
    if (direction.equals("BUY")) {
      result = actualExitLevel - actualEntryLevel;
    } else {
      result = actualEntryLevel - actualExitLevel;
    }
    return result / oneR;
  }
}
