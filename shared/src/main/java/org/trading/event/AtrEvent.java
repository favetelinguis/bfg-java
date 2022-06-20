package org.trading.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;
import org.trading.SystemProperties;
import org.trading.model.AccountEquity;
import org.trading.model.MarketInfo;

@AllArgsConstructor
@Data
@Role(Type.EVENT)
@Expires("5s")
public class AtrEvent extends SystemProperties {
  String epic;
  Double level;

  // TODO some market can take partial size like 1.5 contracts
  public Double positionSize(MarketInfo marketInfo, AccountEquity accountEquity) {
    var equityRiskPerTrade = percentageRiskPerOrder * accountEquity.getEquity();
    var riskPerUnit = level * oneRMultipleOfAtr * marketInfo.getValueOfOnePip();
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
    return oneRMultipleOfAtr * level;
  }
}
