package org.trading.ig.rest.dto.markets.getMarketDetailsListV1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*

*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetMarketDetailsListV1Response {

/*

*/
private java.util.List<MarketDetailsItem> marketDetails;

public java.util.List<MarketDetailsItem> getMarketDetails() { return marketDetails; }
public void setMarketDetails(java.util.List<MarketDetailsItem> marketDetails) { this.marketDetails=marketDetails; }
}
