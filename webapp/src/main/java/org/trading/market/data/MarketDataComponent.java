package org.trading.market.data;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
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
import org.trading.event.MarketClose;
import org.trading.event.MidPrice;
import org.trading.event.OpeningRange;
import org.trading.ig.IgRestService;
import org.trading.ig.rest.dto.prices.getPricesV3.GetPricesV3Response;
import org.trading.ig.rest.dto.prices.getPricesV3.PricesItem;
import org.trading.market.MarketOpen;
import org.trading.market.data.MarketCache.MarketState;

@Component
class MarketDataComponent {

  private static Logger LOG = LoggerFactory.getLogger(MarketDataComponent.class);
  private final IgRestService igRestService;
  private final ApplicationEventPublisher publisher;

  private final MarketCache marketCache = new MarketCache(this);


  @Autowired
  MarketDataComponent(IgRestService igRestService, ApplicationEventPublisher publisher) {
    this.igRestService = igRestService;
    this.publisher = publisher;
  }

  // This event will happen 15minutes after open
  // TODO there is probably a bug here when i do the backfill in the very last milliseconds of a new bar so that i will miss that candle update while i do the backfill
  // TODO check this when running before open, do i get exactly from 9:00 to 9:14 and the first candle update is 9:15???
  @Async
  @EventListener(MarketOpen.class)
  void backfill(MarketOpen marketOpen) {
    var end = LocalDateTime.now().withSecond(0).minusMinutes(1);
    var start = end.minusMinutes(14); // Hardcoded since we know i need 14 periods
    try {
      var response = igRestService.getData(marketOpen.getEpic(), start, end);
      LOG.info("Backfill has {} items", response.getPrices().size());
      var baseBars = createBaseBars(response.getPrices());
      var cache = new MarketState(marketOpen.getEpic());
      // Important baseBars is sorted oldest to newest I think it cant handle sorting
      for (var b : baseBars) {
        cache.addBar(b);
      }
      marketCache.init(cache);
    } catch (Exception e) {
      LOG.error("Failed backfilling data for epic {}", marketOpen.getEpic(), e);
    }
  }

  // Candle events will be sent for markets that I am not trading since they have not opened.
  // For example NASDAQ a 11AM when im trading DAX I will still get candles for NASDAQ since the stream
  // is always subscribed.
  // This will do nothing for epics until we have a backfill that has initiated the cache with epic.
  @EventListener(BarUpdate.class)
  void updateBarSeriesAndPublishAtr(BarUpdate bar) {
    if (marketCache.containsEpic(bar.getEpic())) {
      // Send mid price for every market that is trading atm if this update change bid and ask
      if (bar.getUpdate().containsKey("OFR_CLOSE") && bar.getUpdate().containsKey("BID_CLOSE")) {
        var ofrClose = Double.parseDouble(bar.getUpdate().get("OFR_CLOSE"));
        var bidClose = Double.parseDouble(bar.getUpdate().get("BID_CLOSE"));
        publisher.publishEvent(new MidPrice(bar.getEpic(), (ofrClose + bidClose) / 2));
      }
      var maybeAtr = marketCache.updateAndGetAtr(bar);
      if (maybeAtr.isPresent()) {
        publisher.publishEvent(new Atr(bar.getEpic(), maybeAtr.get()));
      }
    }
  }

  // When the market close remove it from the cache so there will be no new candle updates for it
  @EventListener(MarketClose.class)
  void removeMarket(MarketClose marketClose) {
    var market = marketCache.remove(marketClose.getEpic());
    if (market == null) {
      LOG.info("Trying to remove {} from market cache since its closed but could not find the market.", marketClose.getEpic());
    } else {

      LOG.info("Successfully remove {} so it will no longer process market data since the market is outside trading hours.", marketClose.getEpic());
    }
  }

  @Async
  @EventListener(MarketOpen.class)
  void getOpeningRange(MarketOpen marketOpen) {
    try {
      var start = LocalDate.now().atTime(marketOpen.getOpeningTime());
      var end = start.plusMinutes(marketOpen.getBarsInOpeningRange() - 1); // subtract 1 since end time is excluded
      var result = igRestService.getData(marketOpen.getEpic(), start, end);
      publisher.publishEvent(convertPricesToOpeningRange(marketOpen.getEpic(), result));
    } catch (Exception e) {
      LOG.error("Failed to get opening range for {}", marketOpen.getEpic(), e);
    }
  }

  private OpeningRange convertPricesToOpeningRange(String epic, GetPricesV3Response result) {
    var askMax = result.getPrices().stream().mapToDouble(p -> p.getHighPrice().getAsk().doubleValue()).max().orElseThrow();
    var bidMax = result.getPrices().stream().mapToDouble(p -> p.getHighPrice().getBid().doubleValue()).max().orElseThrow();
    var askMin = result.getPrices().stream().mapToDouble(p -> p.getLowPrice().getAsk().doubleValue()).min().orElseThrow();
    var bidMin = result.getPrices().stream().mapToDouble(p -> p.getLowPrice().getBid().doubleValue()).min().orElseThrow();
    var midHigh = (askMax + bidMax) / 2;
    var midLow = (askMin + bidMin) / 2;
    LOG.info("{} has opening range high:{} low:{}", epic, midHigh, midLow);
    return new OpeningRange(epic, midHigh, midLow);
  }

  // Returns a list where the oldest candle is first
  private List<BaseBar> createBaseBars(List<PricesItem> prices) {
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

