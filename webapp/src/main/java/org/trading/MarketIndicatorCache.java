package org.trading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.ATRIndicator;
import org.trading.drools.DroolsService;

public class MarketIndicatorCache {
  private static Logger LOG = LoggerFactory.getLogger(MarketIndicatorCache.class);
  private final String epic;
  private final BaseBarSeries barSeries;
  private final ATRIndicator atrIndicator;
  private final int atrPeriod = 14;

  public MarketIndicatorCache(String epic) {
    this.epic = epic;
    this.barSeries = new BaseBarSeries(epic);
    barSeries.setMaximumBarCount(100);
    this.atrIndicator = new ATRIndicator(barSeries, atrPeriod);
  }

  public void addBar(BaseBar newBar) {
    try {
      barSeries.addBar(newBar);
    } catch (IllegalArgumentException e) {
      LOG.error("Failed to insert bar ", e);
    }
  }

  public Double getCurrentAtr() {
    return atrIndicator.getValue(barSeries.getEndIndex()).doubleValue();
  }
}
