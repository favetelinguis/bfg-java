package org.trading.ig;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.trading.Market;
import org.trading.drools.DroolsService;
import org.trading.event.Ask;
import org.trading.event.Bid;
import org.trading.ig.rest.AuthenticationResponseAndConversationContext;
import org.trading.ig.rest.ConversationContextV2;
import org.trading.model.MarketInfo;

@Service
public class IgStreamService {
  private static Logger LOG = LoggerFactory.getLogger(IgStreamService.class);

  private final StreamingAPI streamingAPI;
  private AuthenticationResponseAndConversationContext authContext;
  private final ApplicationEventPublisher publisher;
  private final List<String> subscriptionsIds = new ArrayList<>();
  private final String[] epics;

  @Autowired
  public IgStreamService(StreamingAPI streamingAPI, AuthenticationResponseAndConversationContext authContext, Market epics, ApplicationEventPublisher publisher) {
    this.streamingAPI = streamingAPI;
    this.epics = epics.getEpics().stream().map(MarketInfo::getEpic).toArray(String[]::new);
    this.authContext = authContext;
    this.publisher = publisher;
  }

  @PostConstruct
  public void run() {
    streamingAPI.connect(authContext.getAccountId(), (ConversationContextV2) authContext.getConversationContext(), authContext.getLightstreamerEndpoint());
    subscribeToAccount();
    subscribeToConfirms();
    subscribeToOPU();
    subscribeToWOU();
    subscribeToMarkets();
    subscribeToCandles();
  }

  @PreDestroy
  public void destroy() {
    LOG.info("Unsubscribing and disconnecting from IG Stream");
    for (var id : subscriptionsIds) {
      streamingAPI.unsubscribe(id);
    }
    streamingAPI.disconnect();
  }

  private void subscribeToAccount() {
    subscriptionsIds.add(streamingAPI.subscribeForAccountBalanceInfo(authContext.getAccountId(), (item, update) -> {
      LOG.info(item + " =-= " + update.toString());
    }));
  }
  private void subscribeToConfirms() {
    subscriptionsIds.add(streamingAPI.subscribeForConfirms(authContext.getAccountId(), (item, update) -> {
      // TODO get rid of initial conf event
      LOG.info(item + " =-= " + update.toString());
    }));
  }
  private void subscribeToOPU() {
    subscriptionsIds.add(streamingAPI.subscribeForOPUs(authContext.getAccountId(), (item, update) -> {
      LOG.info(item + " =-= " + update.toString());
    }));
  }
  private void subscribeToWOU() {
    subscriptionsIds.add(streamingAPI.subscribeForWOUs(authContext.getAccountId(), (item, update) -> {
      LOG.info(item + " =-= " + update.toString());
    }));
  }
  private void subscribeToMarkets() {
    subscriptionsIds.add(streamingAPI.subscribeForMarkets(epics, (item, update) -> {
//      LOG.info(item + " =-= " + update.toString());
    }));
  }
  private void subscribeToCandles() {
    subscriptionsIds.add(streamingAPI.subscribeForChartCandles(epics, "1MINUTE", (item, update) -> {
      // TODO need to manage when cons is 1 and we have a full candle
      var epic = item.split(":")[1];
      var bid = update.get("BID_CLOSE");
      var ask = update.get("OFR_CLOSE");
      if (ask != null) {
        try {
          publisher.publishEvent(new Ask(epic, Double.parseDouble(ask)));
        } catch (NumberFormatException e) {
          LOG.error("Failed to parse ask {} for epic {}", ask, epic, e);
        }
      }
      if (bid != null) {
        try {
          publisher.publishEvent(new Bid(epic, Double.parseDouble(bid)));
        } catch (NumberFormatException e) {
          LOG.error("Failed to parse bid {} for epic {}", bid, epic, e);
        }
      }
    }));
  }

}