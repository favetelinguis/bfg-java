package org.trading.ig.rest.dto.workingorders.getWorkingOrdersV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*

*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkingOrdersItem {

/*

*/
private WorkingOrderData workingOrderData;

/*

*/
private MarketData marketData;

public WorkingOrderData getWorkingOrderData() { return workingOrderData; }
public void setWorkingOrderData(WorkingOrderData workingOrderData) { this.workingOrderData=workingOrderData; }
public MarketData getMarketData() { return marketData; }
public void setMarketData(MarketData marketData) { this.marketData=marketData; }
}
