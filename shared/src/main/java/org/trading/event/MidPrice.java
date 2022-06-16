package org.trading.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;

@AllArgsConstructor
@Data
@Role(Type.EVENT)
@Expires("5s")
public class MidPrice {
  String epic;
  Double level;
}
