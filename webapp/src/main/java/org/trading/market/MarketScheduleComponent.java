package org.trading.market;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.trading.event.MarketClose;
import org.trading.model.MarketInfo;

/**
 * Schedule event for market open and close with a delta specific to my strategy currently:
 * Market Open 15min after actual open
 * Market Close 5min before actual close
 * The following times are used
 * Open delta 15minutest
 * Close delta 5minutes
 * EU Open 09:00 Close 17:30
 * US Open 15:30  Close 22:00
 * If server is started between market open + delta AND market close - delta events are fired for market open right away
 */
@Component
@Slf4j
public class MarketScheduleComponent {
  private final MarketProps market;
  private ApplicationEventPublisher publisher;
  private final static Long OPEN_DELTA = 15l; // TODO should be atr calc +1?
  private final static Long CLOSE_DELTA = 5l;
  private final static LocalTime US_OPEN = LocalTime.parse("15:30");
  private final static LocalTime US_CLOSE = LocalTime.parse("22:00");
  private final static LocalTime EU_OPEN = LocalTime.parse("09:00");
  private final static LocalTime EU_CLOSE = LocalTime.parse("17:30");

  @Autowired
  public MarketScheduleComponent(MarketProps market, ApplicationEventPublisher publisher) {
    this.market = market;
    this.publisher = publisher;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void checkIfMarketIsOpenNow() {
    var today = LocalDate.now();
    // Exclude weekends which is done in chron schedule and needs to be done here also
    if (!today.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !today.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
      market.getEpics().stream()
          .filter(m -> !m.getNonTradingDays().contains(today)) // Exclude market if it is not trading today
          .forEach( marketInfo -> {
            if (isOpenNow(marketInfo)) {
              sendOpenEvent(marketInfo, marketInfo.getMarketZone().equals("EU") ? EU_OPEN : US_OPEN);
            }
          });
    }
  }

  // will trigger every weekday at 9:15
  @Scheduled(cron = "0 15 9 * * 1-5")
  public void europeOpenPlus15Minutes() {
    var today = LocalDate.now();
    market.getEpics().stream()
        .filter(MarketInfo::isEu)
        .filter(m -> !m.getNonTradingDays().contains(today))
        .forEach( marketInfo -> {
          sendOpenEvent(marketInfo, EU_OPEN);
        });
  }

  @Scheduled(cron = "0 25 17 * * 1-5")
  public void europeCloseMinus5Minutes() {
    var today = LocalDate.now();
    market.getEpics().stream()
        .filter(MarketInfo::isEu)
        .filter(m -> !m.getNonTradingDays().contains(today))
        .forEach( marketInfo -> {
          sendCloseEvent(marketInfo.getEpic());
        });
  }
  @Scheduled(cron = "0 45 15 * * 1-5")
  public void usOpenPlus15Minutes() {
    var today = LocalDate.now();
    market.getEpics().stream()
        .filter(MarketInfo::isUs)
        .filter(m -> !m.getNonTradingDays().contains(today))
        .forEach( marketInfo -> {
          sendOpenEvent(marketInfo, US_OPEN);
        });
  }
  @Scheduled(cron = "0 55 21 * * 1-5")
  public void usCloseMinus5Minutes() {
    var today = LocalDate.now();
    market.getEpics().stream()
        .filter(MarketInfo::isUs)
        .filter(m -> !m.getNonTradingDays().contains(today))
        .forEach( marketInfo -> {
          sendCloseEvent(marketInfo.getEpic());
        });
  }

  private void sendOpenEvent(MarketInfo marketInfo, LocalTime openTime) {
    publisher.publishEvent(new MarketOpen(marketInfo, openTime));
  }
  private void sendCloseEvent(String epic) {
    publisher.publishEvent(new MarketClose(epic));
  }

  private static boolean isOpenNow(MarketInfo marketInfo) {
    var now = LocalTime.now();
    if (marketInfo.isEu()) {
      return now.isAfter(EU_OPEN.plusMinutes(OPEN_DELTA)) && now.isBefore(EU_CLOSE.minusMinutes(CLOSE_DELTA));
    } else if (marketInfo.isUs()) {
      return now.isAfter(US_OPEN.plusMinutes(OPEN_DELTA)) && now.isBefore(US_CLOSE.minusMinutes(CLOSE_DELTA));
    }
    return false;
  }

}
