package org.trading.command;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateWorkingOrderCommand implements Command {
  String epic;
}
