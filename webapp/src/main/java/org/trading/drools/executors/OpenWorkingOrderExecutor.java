package org.trading.drools.executors;

import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trading.EntryPointIds;
import org.trading.ig.IgRestService;

public class OpenWorkingOrderExecutor implements Channel {

  private static Logger LOG = LoggerFactory.getLogger(OpenWorkingOrderExecutor.class);
  private final IgRestService igRestService;
  private final KieSession kieSession;

  public OpenWorkingOrderExecutor(IgRestService igRestService, KieSession kieSession) {
    this.igRestService = igRestService;
    this.kieSession = kieSession;
  }

  @Override
  public void send(Object o) {
    igRestService.createOrder();
    kieSession.getEntryPoint(EntryPointIds.OPENING_RANGE).insert(o);
    kieSession.fireAllRules();
  }
}
