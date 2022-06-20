package org.trading.command;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateWorkingOrderCommand implements Command {
  String epic;
}
