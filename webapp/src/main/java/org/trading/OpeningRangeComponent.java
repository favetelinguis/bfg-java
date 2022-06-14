package org.trading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.trading.event.MarketOpen;
import org.trading.ig.IgRestService;

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

  @EventListener(MarketOpen.class)
  public void getOpeningRange(MarketOpen marketOpen) {
    LOG.info("Market is open get Opening Range for {}", marketOpen.getEpic());
  }
}
