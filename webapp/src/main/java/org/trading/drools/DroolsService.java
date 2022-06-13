package org.trading.drools;

import java.util.ArrayList;
import java.util.List;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trading.ChannelIds;
import org.trading.EntryPointIds;
import org.trading.drools.executors.OpenWorkingOrderExecutor;
import org.trading.event.Ask;
import org.trading.event.Bid;
import org.trading.ig.IgRestService;

@Service
public class DroolsService {
  private static Logger LOG = LoggerFactory.getLogger(DroolsService.class);
  private final KieSession kieSession;
  private final KieScanner kieScanner;
  private final IgRestService igRestService;

  DroolsService(IgRestService igRestService) {
    this.igRestService = igRestService;
    var ks = KieServices.Factory.get();
    var kContainer = ks.newKieContainer(ks.newReleaseId("org.trading", "kjar", "1.0-SNAPSHOT"));
    this.kieSession = kContainer.newKieSession("rules.trade-management.session");
    this.kieScanner = ks.newKieScanner(kContainer);
    startScanner(1000 * 5); // Check for updated rules every 30 seconds
    registerChannels();
  }

  public void stopScanner() {
    kieScanner.stop();
  }

  public void startScanner(long interval) {
    kieScanner.start(interval);
  }

  public void updateBid(Bid bid) {
    triggerKieSessionForEvent(EntryPointIds.BID, bid);
  }

  public void updateAsk(Ask ask) {
    triggerKieSessionForEvent(EntryPointIds.ASK, ask);
  }

  public void updateData(String data) {
    triggerKieSessionForEvent(EntryPointIds.OPENING_RANGE, data);
  }

  private void triggerKieSessionForEvent(String entryPoint, Object event) {
    var entry = kieSession.getEntryPoint(entryPoint);
    if (entryPoint != null) {
      try {
        entry.insert(event);
        kieSession.fireAllRules();
      } catch (Exception e) {
        LOG.error("Failure executing rules", e);
      }
    } else {
      LOG.error("THERE IS NO ENTRY POINT: {}", entryPoint);
    }
  }

  private void registerChannels() {
    kieSession.registerChannel(
        ChannelIds.OPEN_WORKING_ORDER, new OpenWorkingOrderExecutor(igRestService, kieSession));
    kieSession.registerChannel(
        ChannelIds.GET_OPENING_RANGE, new OpenWorkingOrderExecutor(igRestService, kieSession));
  }

  public List<Bid> getAllBids() {
    var results = kieSession.getQueryResults("Get Last 5min bids");
    LOG.warn("NUMBER OF RESULTS ---------------------- {}", results.size());
    var bids = new ArrayList<Bid>();
    for (var result : results) {
      var bid = (Bid) result.get("$b");
      bids.add(bid);
    }
    return bids;
  }
}
