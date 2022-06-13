package org.trading.ig;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trading.drools.DroolsService;
import org.trading.event.Ask;
import org.trading.event.Bid;
import org.trading.ig.rest.AuthenticationResponseAndConversationContext;
import org.trading.ig.rest.ConversationContextV2;

@Service
public class IgStreamService {
  private static Logger LOG = LoggerFactory.getLogger(IgStreamService.class);

  private final StreamingAPI streamingAPI;
  private AuthenticationResponseAndConversationContext authContext;
  private final List<String> subscriptionsIds = new ArrayList<>();
  private final String[] epics;
  private final DroolsService droolsService;

  public IgStreamService(StreamingAPI streamingAPI, AuthenticationResponseAndConversationContext authContext, DroolsService droolsService, IgProps props) {
    this.streamingAPI = streamingAPI;
    this.epics = props.getEpics().toArray(String[]::new);
    this.authContext = authContext;
    this.droolsService = droolsService;
    run();
  }

  public void run() {
    streamingAPI.connect(authContext.getAccountId(), (ConversationContextV2) authContext.getConversationContext(), authContext.getLightstreamerEndpoint());
    subscribeToAccount();
    subscribeToConfirms();
    subscribeToOPU();
    subscribeToWOU();
    subscribeToMarkets();
    subscribeToCandles();
  }

  public void stop() {
    unsubscribeAll();
    streamingAPI.disconnect();
  }

  private void unsubscribeAll() {
    for (var id : subscriptionsIds) {
      streamingAPI.unsubscribe(id);
    }
  }

  private void subscribeToAccount() {
    subscriptionsIds.add(streamingAPI.subscribeForAccountBalanceInfo(authContext.getAccountId(), (item, update) -> {
      LOG.info(item + " =-= " + update.toString());
    }));
  }
  private void subscribeToConfirms() {
    subscriptionsIds.add(streamingAPI.subscribeForConfirms(authContext.getAccountId(), (item, update) -> {
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
          droolsService.updateAsk(new Ask(epic, Double.parseDouble(ask)));
        } catch (NumberFormatException e) {
          LOG.error("Failed to parse ask {} for epic {}", ask, epic, e);
        }
      }
      if (bid != null) {
        try {
          droolsService.updateBid(new Bid(epic, Double.parseDouble(bid)));
        } catch (NumberFormatException e) {
          LOG.error("Failed to parse bid {} for epic {}", bid, epic, e);
        }
      }
    }));
  }
}