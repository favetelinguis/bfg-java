package org.trading.ig.rest.dto.markets.getMarketDetailsListV1;

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
private BigDecimal min;

/*
Band maximum
*/
private BigDecimal max;

/*
Margin Percentage
*/
private BigDecimal margin;

/*
the currency for this currency band factor calculation
*/
private String currency;

public BigDecimal getMin() { return min; }
public void setMin(BigDecimal min) { this.min=min; }
public BigDecimal getMax() { return max; }
public void setMax(BigDecimal max) { this.max=max; }
public BigDecimal getMargin() { return margin; }
public void setMargin(BigDecimal margin) { this.margin=margin; }
public String getCurrency() { return currency; }
public void setCurrency(String currency) { this.currency=currency; }
}
