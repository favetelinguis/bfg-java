package org.trading.ig.rest.dto.markets.getMarketDetailsListV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
Dealing rule
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinNormalStopOrLimitDistance {

/*
Unit
*/
private Unit unit;

/*
Value
*/
private Double value;

public Unit getUnit() { return unit; }
public void setUnit(Unit unit) { this.unit=unit; }
public Double getValue() { return value; }
public void setValue(Double value) { this.value=value; }
}
