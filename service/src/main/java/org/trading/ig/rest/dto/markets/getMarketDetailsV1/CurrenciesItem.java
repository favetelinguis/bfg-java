package org.trading.ig.rest.dto.markets.getMarketDetailsV1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
Currency
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrenciesItem {

/*
Code
*/
private String code;

/*
Name
*/
private String name;

/*
Symbol
*/
private String symbol;

/*
Base exchange rate
*/
private Float baseExchangeRate;

/*
Exchange rate
*/
private Float exchangeRate;

/*
True if this is the default currency
*/
private Boolean isDefault;

public String getCode() { return code; }
public void setCode(String code) { this.code=code; }
public String getName() { return name; }
public void setName(String name) { this.name=name; }
public String getSymbol() { return symbol; }
public void setSymbol(String symbol) { this.symbol=symbol; }
public Float getBaseExchangeRate() { return baseExchangeRate; }
public void setBaseExchangeRate(Float baseExchangeRate) { this.baseExchangeRate=baseExchangeRate; }
public Float getExchangeRate() { return exchangeRate; }
public void setExchangeRate(Float exchangeRate) { this.exchangeRate=exchangeRate; }
public Boolean getIsDefault() { return isDefault; }
public void setIsDefault(Boolean isDefault) { this.isDefault=isDefault; }
}
