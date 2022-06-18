package org.trading.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;
import org.kie.api.definition.type.Timestamp;

@Data
@AllArgsConstructor
@Role(Type.EVENT)
@Expires("5s")
public class AccountEquity {
  String account;
  Double equity;
}
