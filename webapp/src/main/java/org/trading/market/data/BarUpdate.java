package org.trading.market.data;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BarUpdate {
  private String epic;
  private Map<String, String> update;
}
