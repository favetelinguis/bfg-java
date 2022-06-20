package org.trading.market.data;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.DecimalNum;
import org.trading.market.data.MarketCache.CompleteCandle;

@Slf4j
class BarCache {
  private final Map<String, Candle> candleMap = new HashMap<>();

  // Update candle and return a candle if this is a completed candle
  Optional<CompleteCandle> update(String epic, Map<String, String> newCandle) {
    candleMap.putIfAbsent(epic, new Candle(epic));
    var oldCandle = candleMap.get(epic);
    return oldCandle.mergeAndReturnIfComplete(newCandle);
  }

  @Data
  private class Candle {
    private final String epic;
    private Double askOpen;
    private Double askHigh;
    private Double askLow;
    private Double askClose;
    private Double bidOpen;
    private Double bidHigh;
    private Double bidLow;
    private Double bidClose;
    private Long numberTicks;
    private Instant updateTime;

    Candle(String epic) {
      this.epic = epic;
    }

    CompleteCandle getCompleteCandle() {
      var bar = BaseBar.builder(DecimalNum::valueOf, Number.class)
          .timePeriod(Duration.ofMinutes(1))
          .endTime(this.getUpdateTime().atZone(ZoneId.of("Europe/Stockholm")).plusMinutes(1))
          .openPrice((this.getAskOpen() + this.getBidOpen()) / 2)
          .highPrice((this.getAskHigh() + this.getBidHigh()) / 2)
          .lowPrice((this.getAskLow() + this.getBidLow()) / 2)
          .closePrice((this.getAskClose() + this.getBidClose()) / 2)
          .volume(this.getNumberTicks())
          .build();
      return new CompleteCandle(epic, bar);
    }

    Optional<CompleteCandle> mergeAndReturnIfComplete(Map<String, String> newCandle) {
      for (Map.Entry<String, String> entry : newCandle.entrySet()) {
        if (!entry.getValue().isBlank()) {
          switch (entry.getKey()) {
            case "OFR_OPEN":
              this.setAskOpen(Double.parseDouble(entry.getValue()));
              break;
            case "OFR_HIGH":
              this.setAskHigh(Double.parseDouble(entry.getValue()));
              break;
            case "OFR_LOW":
              this.setAskLow(Double.parseDouble(entry.getValue()));
              break;
            case "OFR_CLOSE":
              this.setAskClose(Double.parseDouble(entry.getValue()));
              break;
            case "BID_OPEN":
              this.setBidOpen(Double.parseDouble(entry.getValue()));
              break;
            case "BID_HIGH":
              this.setBidHigh(Double.parseDouble(entry.getValue()));
              break;
            case "BID_LOW":
              this.setBidLow(Double.parseDouble(entry.getValue()));
              break;
            case "BID_CLOSE":
              this.setBidClose(Double.parseDouble(entry.getValue()));
              break;
            case "CONS_TICK_COUNT":
              this.setNumberTicks(Long.parseLong(entry.getValue()));
              break;
            case "UTM":
              this.setUpdateTime(Instant.ofEpochMilli(Long.parseLong(entry.getValue())));
              break;
            default:
              break;
          }
        }
      }
      var maybeHasConsUpdate = newCandle.get("CONS_END") ;
      if (maybeHasConsUpdate != null && maybeHasConsUpdate.equals("1")) {
        return Optional.of(getCompleteCandle());
      }
      return Optional.empty();
    }
  }
}
