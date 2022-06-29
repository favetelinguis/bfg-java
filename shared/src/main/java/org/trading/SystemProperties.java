package org.trading;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.trading.model.MarketInfo;

public final class SystemProperties {
  public final double percentageRiskPerOrder = 0.005; // 0.5% of account each trade
  public final double maxTotalRiskPercentageForAccount = 6.;
  public final double oneRMultipleOfAtr = 2;
  public final double riskRewardRation = 1.;
  public final int barsBeforeExit = 4;
  public final double minAtrMultipleForOpeningRange = 2.;
  public final double bufferMultipleOfAtr = 1.;
  public final int atrPeriod = 25;
  public final int closeDelta = barsBeforeExit + 1; // How many minutes before market close should open orders be closed

  public ZonedDateTime getTodayMarketClose(MarketInfo marketInfo) {
    return LocalDate.now().atTime(marketInfo.getLocalCloseTime()).minusMinutes(closeDelta).atZone(ZoneId.of("Europe/Stockholm"));
  }

  public ZonedDateTime getTodayMarketOpen(MarketInfo marketInfo) {
    return LocalDate.now().atTime(marketInfo.getLocalOpenTime()).plusMinutes(marketInfo.getBarsInOpeningRange()).atZone(ZoneId.of("Europe/Stockholm"));
  }
}
