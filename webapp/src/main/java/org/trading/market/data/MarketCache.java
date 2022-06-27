package org.trading.market.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.adx.MinusDIIndicator;
import org.ta4j.core.indicators.adx.PlusDIIndicator;
import org.trading.SystemProperties;
import org.trading.model.IndicatorState;

@Slf4j
class MarketCache {
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
  synchronized Optional<IndicatorState> updateAndGetIndicatorState(BarUpdate c) {
    // First update Candle
    var maybeCompletedCandle = barCache.update(c.getEpic(), c.getUpdate());
    if (maybeCompletedCandle.isPresent()) {
      // If we get a completed change update the bar series and get atr
      var cache = marketStateMap.get(c.getEpic());
      cache.addBar(maybeCompletedCandle.get().getBar());
      return Optional.of(cache.getCurrentIndicatorState());
    }
    return Optional.empty();
  }

  static class MarketState {
    private SystemProperties systemProperties = new SystemProperties();
    private final String epic;
    private final BaseBarSeries barSeries;
    private final ATRIndicator atrIndicator;
    private PlusDIIndicator plusDIIndicator;
    private MinusDIIndicator minusDIIndicator;
    private ADXIndicator adxIndicator;

    MarketState(String epic) {
      this.epic = epic;
      this.barSeries = new BaseBarSeries(epic);
      barSeries.setMaximumBarCount(100);
      this.atrIndicator = new ATRIndicator(barSeries, systemProperties.atrPeriod);
      this.minusDIIndicator = new MinusDIIndicator(barSeries, systemProperties.atrPeriod);
      this.plusDIIndicator= new PlusDIIndicator(barSeries, systemProperties.atrPeriod);
      this.adxIndicator = new ADXIndicator(barSeries, systemProperties.atrPeriod);
    }

    String getEpic() {
      return epic;
    }

    void addBar(BaseBar newBar) {
      try {
        barSeries.addBar(newBar);
      } catch (IllegalArgumentException e) {
        log.error("Failed to insert bar ", e);
      }

    }

    public IndicatorState getCurrentIndicatorState() {
      var atr = atrIndicator.getValue(barSeries.getEndIndex()).doubleValue();
      var diPlus = plusDIIndicator.getValue(barSeries.getEndIndex()).doubleValue();
      var diMinus = minusDIIndicator.getValue(barSeries.getEndIndex()).doubleValue();
      var adx = adxIndicator.getValue(barSeries.getEndIndex()).doubleValue();
      return new IndicatorState(atr, diPlus, diMinus, adx);
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
