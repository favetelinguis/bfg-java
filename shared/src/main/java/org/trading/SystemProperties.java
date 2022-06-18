package org.trading;

import lombok.Getter;

public abstract class SystemProperties {
  protected final double percentageRiskPerOrder = 0.5;
  protected final double maxTotalRiskPercentageForAccount = 6.;
  protected final double oneRMultipleOfAtr = 2.;
  protected final double riskRewardRation = 2.;
  protected final double minAtrMultipleForOpeningRange = 3.;
  protected final double bufferMultipleOfAtr = 1.;
  protected final int atrPeriod = 14;
}
