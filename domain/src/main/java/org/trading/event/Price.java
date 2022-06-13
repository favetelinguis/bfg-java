package org.trading.event;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;
import org.kie.api.definition.type.Timestamp;

@AllArgsConstructor
@Data
@Role(Type.EVENT)
@Timestamp("eventTime")
public class Price {
  String epic;
  Double level;
  Date eventTime;
}
