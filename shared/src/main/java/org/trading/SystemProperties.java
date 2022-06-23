package org.trading;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.trading.model.MarketInfo;

public final class SystemProperties {
  public final double percentageRiskPerOrder = 0.005; // 0.5% of account each trade
  public final double maxTotalRiskPercentageForAccount = 6.;
  public final double oneRMultipleOfAtr = 3.;
  public final double riskRewardRation = 1.;
  public final double minAtrMultipleForOpeningRange = 3.;
  public final double bufferMultipleOfAtr = 1.;
  public final int atrPeriod = 14;
  public final Long closeDelta = 5l;

  public ZonedDateTime getTodayMarketClose(MarketInfo marketInfo) {
    return LocalDate.now().atTime(marketInfo.getLocalCloseTime()).minusMinutes(closeDelta).atZone(ZoneId.of("Europe/Stockholm"));
  }

  public ZonedDateTime getTodayMarketOpen(MarketInfo marketInfo) {
    return LocalDate.now().atTime(marketInfo.getLocalOpenTime()).plusMinutes(marketInfo.getBarsInOpeningRange()).atZone(ZoneId.of("Europe/Stockholm"));
  }
}
