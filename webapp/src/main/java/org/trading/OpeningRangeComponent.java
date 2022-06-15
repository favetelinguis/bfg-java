package org.trading;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.trading.event.MarketOpen;
import org.trading.event.OpeningRange;
import org.trading.ig.IgRestService;
import org.trading.ig.rest.dto.prices.getPricesV3.GetPricesV3Response;

@Component
public class OpeningRangeComponent {

  private static Logger LOG = LoggerFactory.getLogger(MarketScheduleComponent.class);
  private final IgRestService igRestService;
  private final ApplicationEventPublisher publisher;

  @Autowired
  public OpeningRangeComponent(IgRestService igRestService, ApplicationEventPublisher publisher) {
    this.igRestService = igRestService;
    this.publisher = publisher;
  }

  @Async
  @EventListener(MarketOpen.class)
  public void getOpeningRange(MarketOpen marketOpen) {
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
}
