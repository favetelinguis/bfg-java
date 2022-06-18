package org.trading.drools;

import static org.trading.EntryPointIds.ACCOUNT_EQUITY;
import static org.trading.EntryPointIds.ATR;
import static org.trading.EntryPointIds.CONFIRMS;
import static org.trading.EntryPointIds.MID_PRICE;
import static org.trading.EntryPointIds.OPENING_RANGE;
import static org.trading.EntryPointIds.OPU;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.trading.ChannelIds;
import org.trading.SystemProperties;
import org.trading.command.ClosePositionCommand;
import org.trading.command.CreateWorkingOrderCommand;
import org.trading.command.DeleteWorkingOrderCommand;
import org.trading.command.UpdateWorkingOrderCommand;
import org.trading.event.AccountEquity;
import org.trading.event.Atr;
import org.trading.event.Confirms;
import org.trading.event.MidPrice;
import org.trading.event.OpeningRange;
import org.trading.event.Opu;
import org.trading.ig.IgRestService;
import org.trading.market.MarketProps;

@Service
public class DroolsService {
  private static Logger LOG = LoggerFactory.getLogger(DroolsService.class);
  private final KieSession kieSession;
  private final KieScanner kieScanner;
  private final IgRestService igRestService;
  private final ApplicationEventPublisher publisher;
  private final MarketProps marketProps;

  @Autowired
  DroolsService(IgRestService igRestService, ApplicationEventPublisher publisher, MarketProps marketProps) {
    this.igRestService = igRestService;
    this.publisher = publisher;
    this.marketProps = marketProps;
    var ks = KieServices.Factory.get();
    var kContainer = ks.newKieContainer(ks.newReleaseId("org.trading", "kjar", "1.0-SNAPSHOT"));
    this.kieSession = kContainer.newKieSession("rules.trade-management.session");
    this.kieScanner = ks.newKieScanner(kContainer);
  }

  // TODO will this always run before the first event is fired for kiesession? might need to do in constructor?
  @PostConstruct
  public void initialSetup() {
    startScanner(1000 * 30); // Check for updated rules every 30 seconds
    registerChannels();
    // Insert market info for each market into session
    for (var m : marketProps.getEpics()) {
      kieSession.insert(m);
    }
    kieSession.insert(new SystemProperties());
  }

  public void stopScanner() {
    kieScanner.stop();
  }

  public void startScanner(long interval) {
    kieScanner.start(interval);
  }

  @EventListener(MidPrice.class)
  public void updateBid(MidPrice bid) {
    triggerKieSessionForEvent(MID_PRICE, bid);
  }

  @EventListener(AccountEquity.class)
  public void updateEquity(AccountEquity event) {
    triggerKieSessionForEvent(ACCOUNT_EQUITY, event);
  }

  @EventListener(Opu.class)
  public void updateOpu(Opu event) {
    triggerKieSessionForEvent(OPU, event);
  }

  @EventListener(Confirms.class)
  public void updateConfirms(Confirms event) {
    triggerKieSessionForEvent(CONFIRMS, event);
  }

  @EventListener(OpeningRange.class)
  public void updateConfirms(OpeningRange event) {
    triggerKieSessionForEvent(OPENING_RANGE, event);
  }

  @EventListener(Atr.class)
  public void updateConfirms(Atr event) {
    triggerKieSessionForEvent(ATR, event);
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
        ChannelIds.CREATE_WORKING_ORDER, (o) -> publisher.publishEvent((CreateWorkingOrderCommand) o));
    kieSession.registerChannel(
        ChannelIds.DELETE_WORKING_ORDER, (o) -> publisher.publishEvent((DeleteWorkingOrderCommand) o));
    kieSession.registerChannel(
        ChannelIds.UPDATE_WORKING_ORDER, (o) -> publisher.publishEvent((UpdateWorkingOrderCommand) o));
    kieSession.registerChannel(
        ChannelIds.CLOSE_POSITION, (o) -> publisher.publishEvent((ClosePositionCommand) o));
  }

  public List<MidPrice> queryGetMidPrices() {
    var results = kieSession.getQueryResults("Get Last 5min mid prices");
    LOG.warn("NUMBER OF RESULTS ---------------------- {}", results.size());
    var midPrices = new ArrayList<MidPrice>();
    for (var result : results) {
      var bid = (MidPrice) result.get("$mp");
      midPrices.add(bid);
    }
    return midPrices;
  }
}
