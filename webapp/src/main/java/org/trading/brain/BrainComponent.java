package org.trading.brain;

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.trading.event.AccountEquityEvent;
import org.trading.event.AtrEvent;
import org.trading.event.Confirms;
import org.trading.event.MarketClose;
import org.trading.event.MidPriceEvent;
import org.trading.event.Opu;
import org.trading.event.SystemData;

@Component
@Slf4j
public class BrainComponent {

  private final SystemsHandler systemsHandler = new SystemsHandler();

  @EventListener(MidPriceEvent.class)
  public void updateMidPrice(MidPriceEvent event) {
    systemsHandler.updateMidPrice(event);
  }

  @EventListener(AtrEvent.class)
  public void updateAtr(AtrEvent event) {
    systemsHandler.updateAtr(event);
  }

  @EventListener(AccountEquityEvent.class)
  public void updateEquity(AccountEquityEvent event) {
    systemsHandler.updateAccountEquity(event);
  }

  @EventListener(Opu.class)
  public void updateOpu(Opu event) {
    systemsHandler.updateOpu(event);
  }

  @EventListener(Confirms.class)
  public void updateConfirms(Confirms event) {
    systemsHandler.updateConfirms(event);
  }

  @EventListener(SystemData.class)
  public void updateSystemData(SystemData event) {
    systemsHandler.insertSystem(event);
  }

  @EventListener(MarketClose.class)
  public void updateMarketClose(MarketClose event) {
    systemsHandler.updateMarketClose(event);
  }

  public Collection<SystemData> getActiveSystems() {
    return systemsHandler.getAll();
  }
}
