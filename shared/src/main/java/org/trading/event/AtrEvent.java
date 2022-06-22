package org.trading.event;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

  public Double positionSize(MarketInfo marketInfo, AccountEquityEvent accountEquity) {
    var equityRiskPerTrade = percentageRiskPerOrder * accountEquity.getEquity();
    var riskPerUnit = atr * oneRMultipleOfAtr * marketInfo.getValueOfOnePip();
    var numberContract = equityRiskPerTrade / riskPerUnit;
    return new BigDecimal(numberContract).setScale(2, RoundingMode.FLOOR).doubleValue(); // Make sure to get two decimal place lot size and dont round up so 2.229 is 2.22
  }

  public Double targetDistance() {
    return riskRewardRation * stopDistance();
  }

  public Double stopDistance() {
    return oneRMultipleOfAtr * atr;
  }
}
