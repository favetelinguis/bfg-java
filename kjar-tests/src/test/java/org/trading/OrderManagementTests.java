package org.trading;

import javax.inject.Inject;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.cdi.KReleaseId;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.trading.model.OpeningRange;

public class OrderManagementTests {


  KieServices ks = KieServices.Factory.get();
  KieContainer kContainer = ks.newKieContainer(ks.newReleaseId("org.trading", "kjar", "1.0-SNAPSHOT"));

  @Test
  public void canLoadRules() {
    var results = kContainer.verify();
    results.getMessages().stream().forEach(message -> {
      System.out.println(">> Message ( "+message.getLevel()+" ): "+message.getText());
    });
    assertFalse(results.hasMessages(Message.Level.ERROR));
  }

  @Test
  public void canCreateOrder() {
    var kieSession = kContainer.newKieSession("rules.trade-management.session");
    var openingRange = new OpeningRange();
    openingRange.setHigh(11.);
    openingRange.setLow(10.);
    kieSession.insert(openingRange);
    var numberFired = kieSession.fireAllRules();
    assertEquals(1, numberFired);
  }
}
