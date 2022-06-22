package org.trading.market;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.trading.model.MarketInfo;

/**
 * Opening time is the actual time the market opens without and delta.
 */
@AllArgsConstructor
@Data
public class MarketOpen {
  MarketInfo marketInfo;
}
