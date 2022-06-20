package org.trading.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;

@Data
@AllArgsConstructor
public class AccountEquity {
  String account;
  Double equity;
}
