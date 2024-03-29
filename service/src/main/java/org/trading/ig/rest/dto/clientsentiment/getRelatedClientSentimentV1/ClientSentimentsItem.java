package org.trading.ig.rest.dto.clientsentiment.getRelatedClientSentimentV1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
Client market sentiment
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientSentimentsItem {

/*
Market identifier
*/
private String marketId;

/*
Percentage long positions
*/
private Float longPositionPercentage;

/*
Percentage short positions
*/
private Float shortPositionPercentage;

public String getMarketId() { return marketId; }
public void setMarketId(String marketId) { this.marketId=marketId; }
public Float getLongPositionPercentage() { return longPositionPercentage; }
public void setLongPositionPercentage(Float longPositionPercentage) { this.longPositionPercentage=longPositionPercentage; }
public Float getShortPositionPercentage() { return shortPositionPercentage; }
public void setShortPositionPercentage(Float shortPositionPercentage) { this.shortPositionPercentage=shortPositionPercentage; }
}
