package org.trading.event;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.trading.model.MarketInfo;

@Data
@Slf4j
public class AccountEquityEvent {
  private String account;
  private Double equity;
  private Double availableCache;

  public AccountEquityEvent(String account) {
    this.account = account;
  }

  public boolean hasEnoughMarginLeft(Double size, MidPriceEvent currentMidPrice, MarketInfo marketInfo) {
    var cacheNeeded = size * currentMidPrice.getLevel() * marketInfo.getMarginRequirement();
    var isOk = availableCache > cacheNeeded;
    if (!isOk) {
      log.warn("Not enough cash ({}) left to cover margin requirement ({})", availableCache, cacheNeeded);
    }
    return isOk;
  }
}
