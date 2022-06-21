package org.trading.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.trading.model.OrderHandler;
import org.trading.model.Position;

@Data
@AllArgsConstructor
public class UpdatePositionCommand implements Command {
  String epic;
  String dealId;
  Double stopLevel;
  Double trailingStopDistance;
  Double targetLevel;

  public static Command from(String epic, Position position) {
    return new UpdatePositionCommand(epic, position.getOrder().getDealId(), position.getStopLevel(), position.getTrailingStopDistance(), position.getTargetLevel());
  }
}
