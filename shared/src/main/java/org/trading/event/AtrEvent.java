package org.trading.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.trading.SystemProperties;
import org.trading.model.AccountEquity;
import org.trading.model.MarketInfo;

@Data
public class AtrEvent {
  private String epic;
  private Double atr;
  @JsonIgnore
  private SystemProperties systemProperties;
  public AtrEvent(String epic, Double atr) {
    this.epic = epic;
    this.atr = atr;
  }

  public Double positionSize(MarketInfo marketInfo, AccountEquityEvent accountEquity) {
    var equityRiskPerTrade = systemProperties.percentageRiskPerOrder * accountEquity.getEquity();
    var riskPerUnit = atr * systemProperties.oneRMultipleOfAtr * marketInfo.getValueOfOnePip();
    var numberContract = equityRiskPerTrade / riskPerUnit;
    return new BigDecimal(numberContract).setScale(2, RoundingMode.FLOOR).doubleValue(); // Make sure to get two decimal place lot size and dont round up so 2.229 is 2.22
  }

  public Double targetDistance() {
    return systemProperties.riskRewardRation * stopDistance();
  }

  public Double stopDistance() {
    return systemProperties.oneRMultipleOfAtr * atr;
  }
}
