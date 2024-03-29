package org.trading.ig.rest.dto.markets.getMarketDetailsV1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
Time range
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketTimesItem {

/*
Open time
*/
private String openTime;

/*
Close time
*/
private String closeTime;

public String getOpenTime() { return openTime; }
public void setOpenTime(String openTime) { this.openTime=openTime; }
public String getCloseTime() { return closeTime; }
public void setCloseTime(String closeTime) { this.closeTime=closeTime; }
}
