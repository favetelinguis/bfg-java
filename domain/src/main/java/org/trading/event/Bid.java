package org.trading.event;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;
import org.kie.api.definition.type.Timestamp;

@AllArgsConstructor
@Data
@Role(Type.EVENT)
@Expires("5s")
public class Bid {
  String epic;
  Double level;
}
