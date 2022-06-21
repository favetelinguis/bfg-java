package org.trading.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.trading.SystemProperties;
import org.trading.model.AccountEquity;
import org.trading.model.MarketInfo;

@AllArgsConstructor
@Data
public class AtrEvent extends SystemProperties {
  String epic;
  Double atr;

  // TODO some market can take partial size like 1.5 contracts
  public Double positionSize(MarketInfo marketInfo, AccountEquityEvent accountEquity) {
    var equityRiskPerTrade = percentageRiskPerOrder * accountEquity.getEquity();
    var riskPerUnit = atr * oneRMultipleOfAtr * marketInfo.getValueOfOnePip();
    var numberContract = Math.floor(equityRiskPerTrade / riskPerUnit); // Round down
    // Make sure number of contract are larger then the minimum for the market
    if (marketInfo.getLotSize() > numberContract) {
      return 0.;
    }
    return numberContract;
  }

  public Double targetDistance() {
    return riskRewardRation * stopDistance();
  }

  public Double stopDistance() {
    return oneRMultipleOfAtr * atr;
  }
}
