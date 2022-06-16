package org.trading.market;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import org.ta4j.core.BaseBar;

@AllArgsConstructor
@Getter
@ToString
public class CompleteCandle {
  private final String epic;
  private final BaseBar bar;
}
