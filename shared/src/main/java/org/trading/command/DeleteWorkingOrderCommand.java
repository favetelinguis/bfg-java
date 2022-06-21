package org.trading.command;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteWorkingOrderCommand implements Command {
  String epic;
  String dealId;

  public static Command from(String epic, String otherDealId) {
    return new DeleteWorkingOrderCommand(epic, otherDealId);
  }
}
