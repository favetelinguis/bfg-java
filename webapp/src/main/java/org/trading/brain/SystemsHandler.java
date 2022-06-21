package org.trading.brain;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.trading.event.AccountEquityEvent;
import org.trading.event.AtrEvent;
import org.trading.event.Confirms;
import org.trading.event.MarketClose;
import org.trading.event.MidPriceEvent;
import org.trading.event.Opu;
import org.trading.event.SystemData;

@Slf4j
public class SystemsHandler {
  private final Map<String, SystemData> systemHandler = new HashMap<>();
  private AccountEquityEvent accountEquityEvent;

  public synchronized void insertSystem(SystemData event) {
    event.setCurrentAccountEquity(accountEquityEvent);
    if (systemHandler.putIfAbsent(event.getEpic(), event) != null) {
      log.error("Trying to insert system that already exist {}", event.getEpic());
    }
  }

  public synchronized void updateMidPrice(MidPriceEvent event) {
    if (systemHandler.containsKey(event.getEpic())) {
      systemHandler.get(event.getEpic()).updateMidPrice(event);
    }
  }

  public synchronized void updateAtr(AtrEvent event) {
    if (systemHandler.containsKey(event.getEpic())) {
      systemHandler.get(event.getEpic()).updateAtr(event);
    }
  }

  public synchronized void updateOpu(Opu event) {
    if (systemHandler.containsKey(event.getEpic())) {
      systemHandler.get(event.getEpic()).updateOrderStatus(event);
    }
  }
  public synchronized void updateConfirms(Confirms event) {
    if (systemHandler.containsKey(event.getEpic())) {
      systemHandler.get(event.getEpic()).updateOrderStatus(event);
    }
  }
  public synchronized void updateMarketClose(MarketClose event) {
    if (systemHandler.containsKey(event.getEpic())) {
      systemHandler.get(event.getEpic()).updateOrderStatus(event);
      systemHandler.remove(event.getEpic());
    }
  }

  public synchronized void updateAccountEquity(AccountEquityEvent event) {
    this.accountEquityEvent = event;
  }

}
