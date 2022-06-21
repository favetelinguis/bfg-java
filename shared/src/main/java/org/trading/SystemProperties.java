package org.trading;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.trading.model.MarketInfo;

public abstract class SystemProperties {
  protected final double percentageRiskPerOrder = 0.005; // 0.5% of account each trade
  protected final double maxTotalRiskPercentageForAccount = 6.;
  protected final double oneRMultipleOfAtr = 2.;
  protected final double riskRewardRation = 2.;
  protected final double minAtrMultipleForOpeningRange = 3.;
  protected final double bufferMultipleOfAtr = 1.;
  protected final int atrPeriod = 14;
  protected final static Long OPEN_DELTA = 15l; // TODO should be atr calc +1?
  protected final static Long CLOSE_DELTA = 5l;
  protected final static LocalTime US_OPEN = LocalTime.parse("15:30");
  protected final static LocalTime US_CLOSE = LocalTime.parse("22:00");
  protected final static LocalTime EU_OPEN = LocalTime.parse("09:00");
  protected final static LocalTime EU_CLOSE = LocalTime.parse("17:30");

  protected ZonedDateTime getTodayMarketClose(MarketInfo marketInfo) {
    if (marketInfo.isEu()) {
      return LocalDate.now().atTime(EU_CLOSE).minusMinutes(CLOSE_DELTA).atZone(ZoneId.of("Europe/Stockholm"));
    }
    return LocalDate.now().atTime(US_CLOSE).minusMinutes(CLOSE_DELTA).atZone(ZoneId.of("Europe/Stockholm"));
  }

  protected ZonedDateTime getTodayMarketOpen(MarketInfo marketInfo) {
    if (marketInfo.isEu()) {
      return LocalDate.now().atTime(EU_OPEN).plusMinutes(OPEN_DELTA).atZone(ZoneId.of("Europe/Stockholm"));
    }
    return LocalDate.now().atTime(US_CLOSE).plusMinutes(OPEN_DELTA).atZone(ZoneId.of("Europe/Stockholm"));
  }
}
