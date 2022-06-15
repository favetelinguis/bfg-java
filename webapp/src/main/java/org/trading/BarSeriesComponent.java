package org.trading;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.DecimalNum;
import org.trading.event.Atr;
import org.trading.event.Candle;
import org.trading.event.MarketOpen;
import org.trading.ig.IgRestService;
import org.trading.ig.rest.dto.prices.getPricesV3.PricesItem;

@Component
public class BarSeriesComponent {

  private static Logger LOG = LoggerFactory.getLogger(BarSeriesComponent.class);
  private final IgRestService igRestService;
  private final ApplicationEventPublisher publisher;
  private final Map<String, MarketIndicatorCache> indicatorCacheMap = new HashMap<>();


  @Autowired
  public BarSeriesComponent(IgRestService igRestService, ApplicationEventPublisher publisher) {
    this.igRestService = igRestService;
    this.publisher = publisher;
  }

  // This event will happen 15minutes after open
  // TODO there is probably a bug here when i do the backfill in the very last milliseconds of a new bar so that i will miss that candle update while i do the backfill
  // TODO check this when running before open, do i get exactly from 9:00 to 9:14 and the first candle update is 9:15???
  @Async
  @EventListener(MarketOpen.class)
  public void backfill(MarketOpen marketOpen) {
    var end = LocalDateTime.now().withSecond(0).minusMinutes(1);
    var start = end.minusMinutes(15); // Hardcoded since we know i need 14 periods
    try {
      var response = igRestService.getData(marketOpen.getEpic(), start, end);
      LOG.info("Backfill has {} items", response.getPrices().size());
      var baseBars = createBaseBars(response.getPrices());
      var cache = new MarketIndicatorCache(marketOpen.getEpic());
      // Important baseBars is sorted oldest to newest I think it cant handle sorting
      for (var b : baseBars) {
        cache.addBar(b);
      }
      indicatorCacheMap.put(marketOpen.getEpic(), cache);
    } catch (Exception e) {
      LOG.error("Failed backfilling data for epic {}", marketOpen.getEpic(), e);
    }
  }

  // Candle events will be sent for markets that I am not trading since they have not opened.
  // For example NASDAQ a 11AM when im trading DAX I will still get candles for NASDAQ since the stream
  // is always subscribed.
  // This will do nothing for epics until we have a backfill that has initiated the cache with epic.
  @Async
  @EventListener(Candle.class)
  public void updateBarSeriesAndPublishAtr(Candle c) {
    if (indicatorCacheMap.containsKey(c.getEpic())) {
      LOG.info("Candledata {}", c);
      var cache = indicatorCacheMap.get(c.getEpic());
      var zoneTime = c.getUpdateTime().atZone(ZoneId.of("Europe/Stockholm"));
      var bar = BaseBar.builder(DecimalNum::valueOf, Number.class)
          .timePeriod(Duration.ofMinutes(1))
          .endTime(zoneTime.plusMinutes(1))
          .openPrice((c.getAskOpen() + c.getBidOpen()) / 2)
          .highPrice((c.getAskHigh() + c.getBidHigh()) / 2)
          .lowPrice((c.getAskLow() + c.getBidLow()) / 2)
          .closePrice((c.getAskClose() + c.getBidClose()) / 2)
          .volume(c.getNumberTicks())
          .build();
      cache.addBar(bar);
      publisher.publishEvent(new Atr(c.getEpic(), cache.getCurrentAtr()));
    }
  }

  // Returns a list where the oldest candle is first
  List<BaseBar> createBaseBars(List<PricesItem> prices) {
    var baseBars = new ArrayList<BaseBar>();
    for (var p : prices) {
      var instant = Instant.parse(p.getSnapshotTimeUTC() + "Z");
      BaseBar bar = BaseBar.builder(DecimalNum::valueOf, Number.class)
          .timePeriod(Duration.ofMinutes(1))
          .endTime(instant.atZone(ZoneId.of("Europe/Stockholm")).plusMinutes(1))
          .openPrice((p.getOpenPrice().getAsk().doubleValue() + p.getOpenPrice().getBid().doubleValue())/2)
          .highPrice((p.getHighPrice().getAsk().doubleValue() + p.getHighPrice().getBid().doubleValue())/2)
          .lowPrice((p.getLowPrice().getAsk().doubleValue() + p.getLowPrice().getBid().doubleValue())/2)
          .closePrice((p.getClosePrice().getAsk().doubleValue() + p.getClosePrice().getBid().doubleValue())/2)
          .volume(p.getLastTradedVolume())
          .build();
      baseBars.add(bar);
    }
    return baseBars;
  }
}
