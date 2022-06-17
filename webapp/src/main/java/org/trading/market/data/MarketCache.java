package org.trading.market.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.ATRIndicator;
import org.trading.SystemProperties;

class MarketCache {
  private static Logger LOG = LoggerFactory.getLogger(MarketCache.class);

  private final MarketDataComponent marketDataComponent;
  private final Map<String, MarketState> marketStateMap = new HashMap<>();
  private final BarCache barCache = new BarCache();

  MarketCache(MarketDataComponent marketDataComponent) {
    this.marketDataComponent = marketDataComponent;
  }

  synchronized void init(MarketState state) {
    if (marketStateMap.putIfAbsent(state.getEpic(), state) != null) {

    }
  }

  synchronized boolean containsEpic(String epic) {
    return marketStateMap.containsKey(epic);
  }

  synchronized MarketState remove(String epic) {
    return marketStateMap.remove(epic);
  }

  // We know here that we always have a valu for the epic in the map since that is checked before
  synchronized Optional<Double> updateAndGetAtr(BarUpdate c) {
    // First update Candle
    var maybeCompletedCandle = barCache.update(c.getEpic(), c.getUpdate());
    if (maybeCompletedCandle.isPresent()) {
      LOG.info("Completed change for {} - {}", c.getEpic(), c.getUpdate());
      // If we get a completed change update the bar series and get atr
      var cache = marketStateMap.get(c.getEpic());
      cache.addBar(maybeCompletedCandle.get().getBar());
      return Optional.of(cache.getCurrentAtr());
    }
    return Optional.empty();
  }

  static class MarketState {
    private final String epic;
    private final BaseBarSeries barSeries;
    private final ATRIndicator atrIndicator;

    MarketState(String epic) {
      this.epic = epic;
      this.barSeries = new BaseBarSeries(epic);
      barSeries.setMaximumBarCount(100);
      this.atrIndicator = new ATRIndicator(barSeries, SystemProperties.atrPeriod);
    }

    String getEpic() {
      return epic;
    }

    void addBar(BaseBar newBar) {
      try {
        barSeries.addBar(newBar);
      } catch (IllegalArgumentException e) {
        LOG.error("Failed to insert bar ", e);
      }

    }

    Double getCurrentAtr() {
      return atrIndicator.getValue(barSeries.getEndIndex()).doubleValue();
    }
  }

  @AllArgsConstructor
  @Getter
  @ToString
  static class CompleteCandle {
    private final String epic;
    private final BaseBar bar;
  }
}
