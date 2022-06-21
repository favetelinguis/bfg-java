package org.trading.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountEquityEvent {
  String account;
  Double equity;
}
