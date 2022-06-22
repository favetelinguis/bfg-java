package org.trading.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.trading.model.MarketInfo;

@AllArgsConstructor
@Data
public class MarketClose {
  private MarketInfo marketInfo;
}
