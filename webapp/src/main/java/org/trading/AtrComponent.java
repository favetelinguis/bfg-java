package org.trading;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.trading.event.Candle;
import org.trading.event.MarketOpen;
import org.trading.ig.IgRestService;

@Component
public class AtrComponent {

  private static Logger LOG = LoggerFactory.getLogger(AtrComponent.class);
  private final IgRestService igRestService;
  private final ApplicationEventPublisher publisher;

  @Autowired
  public AtrComponent(IgRestService igRestService, ApplicationEventPublisher publisher) {
    this.igRestService = igRestService;
    this.publisher = publisher;
  }

  @Async
  @EventListener(MarketOpen.class)
  public void backfill(MarketOpen marketOpen) {
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    LOG.info("Market is open get backfill for ATR {}", marketOpen.getEpic());
  }

  // Candle events will be sent for markets that I am not trading since they have not opened.
  // For example NASDAQ a 11AM when im trading DAX I will still get candles for NASDAQ since the stream
  // is always subscribed.
  @Async
  @EventListener(Candle.class)
  public void updateAtr(Candle candle) {
    LOG.info("New Completed Candle, update ATR {}", candle.getEpic());
  }
}
