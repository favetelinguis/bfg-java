package org.trading.brain;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
    if (accountEquityEvent != null) {
      event.setCurrentAccountEquity(accountEquityEvent);
      if (systemHandler.putIfAbsent(event.getEpic(), event) != null) {
        log.error("Trying to insert system that already exist {}", event.getEpic());
      }
    } else {
      log.error("Trying to initialize System for {} when AccountEquity is null is not allowed", event.getEpic());
    }
  }

  public synchronized void updateMidPrice(MidPriceEvent event) {
    if (systemHandler.containsKey(event.getEpic())) {
      systemHandler.get(event.getEpic()).handleMidPriceEvent(event);
    }
  }

  public synchronized void updateAtr(AtrEvent event) {
    if (systemHandler.containsKey(event.getEpic())) {
      systemHandler.get(event.getEpic()).handleAtrEvent(event);
    }
  }

  public synchronized void updateOpu(Opu event) {
    if (systemHandler.containsKey(event.getEpic())) {
      systemHandler.get(event.getEpic()).handleOpuEvent(event);
    }
  }
  public synchronized void updateConfirms(Confirms event) {
    if (systemHandler.containsKey(event.getEpic())) {
      systemHandler.get(event.getEpic()).handleConfirmsEvent(event);
    }
  }
  public synchronized void updateMarketClose(MarketClose event) {
    var maybeSystem = systemHandler.remove(event.getMarketInfo().getEpic());
    if (maybeSystem != null) {
      maybeSystem.closeMarket(); // Will close any open positions
    }
  }

  public synchronized void updateAccountEquity(AccountEquityEvent event) {
    if (this.accountEquityEvent == null) {
      this.accountEquityEvent = event;
    } else {
      if (event.getEquity() != null) {
        this.accountEquityEvent.setEquity(event.getEquity());
      }
      if (event.getAvailableCache() != null) {
        this.accountEquityEvent.setAvailableCache(event.getAvailableCache());
      }
    }
  }

  public Collection<SystemData> getAll() {
    return systemHandler.values();
  }
}
