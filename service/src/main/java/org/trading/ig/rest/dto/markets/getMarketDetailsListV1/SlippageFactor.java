package org.trading.ig.rest.dto.markets.getMarketDetailsListV1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
Slippage factor details for a given market
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlippageFactor {

/*
Unit
*/
private String unit;

/*
Value
*/
private Double value;

public String getUnit() { return unit; }
public void setUnit(String unit) { this.unit=unit; }
public Double getValue() { return value; }
public void setValue(Double value) { this.value=value; }
}
