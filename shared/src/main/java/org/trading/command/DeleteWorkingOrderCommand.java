package org.trading.command;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteWorkingOrderCommand implements Command {
  String epic;
}
