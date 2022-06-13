package org.trading.event;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;
import org.kie.api.definition.type.Timestamp;

@Data
@AllArgsConstructor
@Role(Type.EVENT)
@Timestamp("eventTime")
public class PositionUpdate {
  String epic;
  String direction;
  String status;
  Date eventTime;
}
