package org.trading.market;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.trading.SystemProperties;
import org.trading.event.MarketClose;
import org.trading.model.MarketInfo;

@Slf4j
@Component
public class MarketScheduleComponent {

  private SystemProperties systemProperties = new SystemProperties();
  private final ThreadPoolTaskScheduler taskScheduler;
  private final ApplicationEventPublisher publisher;
  private final MarketProps markets;

  @Autowired
  public MarketScheduleComponent(ThreadPoolTaskScheduler taskScheduler, ApplicationEventPublisher publisher, MarketProps markets) {
    this.taskScheduler = taskScheduler;
    this.publisher = publisher;
    this.markets = markets;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void initMarkets() {
    ifMarketOpenNowTrigger();
    setupSchedulers();
  }

  private void ifMarketOpenNowTrigger() {
    var today = LocalDate.now();
    // Exclude weekends which is done in chron schedule and needs to be done here also
    if (!today.getDayOfWeek().equals(DayOfWeek.SATURDAY) && !today.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
      markets.getEpics().stream()
          .filter(m -> !m.getNonTradingDays().contains(today)) // Exclude market if it is not trading today
          .forEach( marketInfo -> {
            if (isOpenNow(marketInfo)) {
              sendOpenEvent(marketInfo);
            }
          });
    }
  }

  private void setupSchedulers() {
    // Skapa CronTrigger basert på market info open and close + number of bars in open
    // I lambda filtrera på non trading days och trigga
    // Om marknaden är uppen just nu hur trigga direkt?
    markets.getEpics().stream().forEach(marketInfo -> {
      var bars = marketInfo.getBarsInOpeningRange();
      var openHour = String.valueOf(marketInfo.getLocalOpenTime().getHour());
      var  openMinute = String.valueOf(marketInfo.getLocalOpenTime().plusMinutes(bars).getMinute());
      var closeHour = String.valueOf(marketInfo.getLocalCloseTime().getHour());
      var  closeMinute = String.valueOf(marketInfo.getLocalCloseTime().minusMinutes(systemProperties.closeDelta).getMinute());
      var start = taskScheduler.schedule(() -> sendOpenEvent(marketInfo), new CronTrigger("0 " + openMinute + " " + openHour + " * * 1-5"));
      var close = taskScheduler.schedule(() -> sendCloseEvent(marketInfo), new CronTrigger("0 " + closeMinute + " " + closeHour + " * * 1-5"));
    });
  }

  private void sendOpenEvent(MarketInfo marketInfo) {
    var today = LocalDate.now();
    if (!marketInfo.getNonTradingDays().contains(today)) {
      publisher.publishEvent(new MarketOpen(marketInfo));
    }
  }
  private void sendCloseEvent(MarketInfo marketInfo) {
    var today = LocalDate.now();
    if (!marketInfo.getNonTradingDays().contains(today)) {
      publisher.publishEvent(new MarketClose(marketInfo));
    }
  }

  private boolean isOpenNow(MarketInfo marketInfo) {
    var now = LocalTime.now();
      return
          now.isAfter(marketInfo.getLocalOpenTime().plusMinutes(marketInfo.getBarsInOpeningRange()))
              && now.isBefore(
              marketInfo.getLocalCloseTime().minusMinutes(systemProperties.closeDelta));
  }
}
