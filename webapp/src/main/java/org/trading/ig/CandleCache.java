package org.trading.ig;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.trading.event.Candle;

public class CandleCache {
  private final Map<String, Candle> candleMap = new HashMap<>();

  // Update candle and return a candle if this is a completed candle
  public Optional<Candle> update(String epic, Map<String, String> newCandle) {
    if (newCandle.get("CONS_END").equals("1")) {
      var oldCandle = candleMap.remove(epic);
      if (oldCandle == null) {
        return Optional.empty();
      }
      return mergeAndReturnCompletedCandle(oldCandle, newCandle);
    } else {
      candleMap.compute(epic, (k,v) -> mergeCandles(epic, v, newCandle));
      return Optional.empty();
    }
  }

  private Optional<Candle> mergeAndReturnCompletedCandle(Candle oldCandle, Map<String, String> newCandle) {
    return Optional.of(mergeCandles(oldCandle.getEpic(), oldCandle, newCandle));
  }

  private Candle mergeCandles(String epic, Candle oldCandle, Map<String, String> newCandle) {
    if (oldCandle == null) {
      oldCandle = new Candle(epic);
    }
    for (Map.Entry<String, String> entry : newCandle.entrySet()) {
      switch (entry.getKey()) {
        case "OFR_OPEN":
          oldCandle.setAskOpen(Double.parseDouble(entry.getValue()));
          break;
        case "OFR_HIGH":
          oldCandle.setAskHigh(Double.parseDouble(entry.getValue()));
          break;
        case "OFR_LOW":
          oldCandle.setAskLow(Double.parseDouble(entry.getValue()));
          break;
        case "OFR_CLOSE":
          oldCandle.setAskClose(Double.parseDouble(entry.getValue()));
          break;
        case "BID_OPEN":
          oldCandle.setBidOpen(Double.parseDouble(entry.getValue()));
          break;
        case "BID_HIGH":
          oldCandle.setBidHigh(Double.parseDouble(entry.getValue()));
          break;
        case "BID_LOW":
          oldCandle.setBidLow(Double.parseDouble(entry.getValue()));
          break;
        case "BID_CLOSE":
          oldCandle.setBidClose(Double.parseDouble(entry.getValue()));
          break;
        case "CONS_TICK_COUNT":
          oldCandle.setNumberTicks(Long.parseLong(entry.getValue()));
          break;
        case "UTM":
          oldCandle.setUpdateTime(Instant.ofEpochMilli(Long.parseLong(entry.getValue())));
          break;
        default:
          break;
      }
    }
    return oldCandle;
  }
}
