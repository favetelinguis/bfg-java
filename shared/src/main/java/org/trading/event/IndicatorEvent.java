package org.trading.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import lombok.Data;
import org.trading.SystemProperties;
import org.trading.model.IndicatorState;
import org.trading.model.MarketInfo;

@Data
public class IndicatorEvent {
  private String epic;
  private IndicatorState indicatorState;
  @JsonIgnore
  private SystemProperties systemProperties = new SystemProperties();
  public IndicatorEvent(String epic, IndicatorState indicatorState) {
    this.epic = epic;
    this.indicatorState = indicatorState;
  }

  public Double positionSize(MarketInfo marketInfo, AccountEquityEvent accountEquity) {
    var equityRiskPerTrade = systemProperties.percentageRiskPerOrder * accountEquity.getEquity();
    var riskPerUnit = indicatorState.getAtr() * systemProperties.oneRMultipleOfAtr * marketInfo.getValueOfOnePip();
    var numberContract = equityRiskPerTrade / riskPerUnit;
    return new BigDecimal(numberContract).setScale(2, RoundingMode.FLOOR).doubleValue(); // Make sure to get two decimal place lot size and dont round up so 2.229 is 2.22
  }

  public Double targetDistance() {
    return systemProperties.riskRewardRation * stopDistance();
  }

  public Double stopDistance() {
    return systemProperties.oneRMultipleOfAtr * indicatorState.getAtr();
  }

  /**
   * Indicate up when DI+ is 10 points above DI-,
   * Indicate down when DI- is 10 points under DI+
   * If the difference is smaller than 10 points no signal is indicated
   */
  public boolean isBuyAllowed() {
//    var diff = indicatorState.getDiMinus() > indicatorState.getDiPlus();
//    return !(diff && (indicatorState.getAdx() >= 25));
    return true;
  }

  public boolean isSellAllowed() {
//    var diff = indicatorState.getDiMinus() < indicatorState.getDiPlus();
//    return !(diff && (indicatorState.getAdx() >= 25));
    return true;
  }
}
