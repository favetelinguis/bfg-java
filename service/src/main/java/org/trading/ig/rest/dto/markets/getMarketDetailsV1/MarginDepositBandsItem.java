package org.trading.ig.rest.dto.markets.getMarketDetailsV1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/*
Deposit band
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarginDepositBandsItem {

/*
Band minimum
*/
private Integer min;

/*
Band maximum
*/
private Integer max;

/*
Margin Percentage
*/
private BigDecimal margin;

public Integer getMin() { return min; }
public void setMin(Integer min) { this.min=min; }
public Integer getMax() { return max; }
public void setMax(Integer max) { this.max=max; }
public BigDecimal getMargin() { return margin; }
public void setMargin(BigDecimal margin) { this.margin=margin; }
}
