package org.trading.ig.rest.dto.markets.getMarketDetailsListV1;
/*
Trailing stops trading preference for the specified market
*/
public enum TrailingStopsPreference {

/*
Trailing stops are allowed for the current market
*/
AVAILABLE,
/*
Trailing stops are not allowed for the current market
*/
NOT_AVAILABLE
}
