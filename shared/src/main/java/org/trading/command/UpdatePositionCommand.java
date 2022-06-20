package org.trading.command;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdatePositionCommand implements Command {
  String epic;
}
