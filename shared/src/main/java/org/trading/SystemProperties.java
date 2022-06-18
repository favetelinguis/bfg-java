package org.trading;

import lombok.Getter;

@Getter
public class SystemProperties {
  Double percentageRiskPerOrder = 0.5;
  Double maxTotalRiskPercentageForAccount = 6.;
  Double oneRAsMultipleOfAtr = 2.;
  Double riskRewardRation = 2.;
  Double minAtrMultipleForOpeningRange = 3.;
  int atrPeriod = 14;
}
