package org.trading.ig.rest.dto.positions.otc.createOTCPositionV1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/*
Create position request
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateOTCPositionV1Request {

/*
A user-defined reference identifying the submission of the order
*/
private String dealReference;

/*
Instrument epic identifier
*/
private String epic;

/*
Instrument expiry
*/
private String expiry;

/*
Deal direction
*/
private Direction direction;

/*
Deal size
*/
private BigDecimal size;

/*
Deal level
*/
private BigDecimal level;

/*
Order type
*/
private OrderType orderType;

/*
True if a guaranteed stop is required
*/
private Boolean guaranteedStop;

/*
Stop level
*/
private BigDecimal stopLevel;

/*
Stop distance
*/
private BigDecimal stopDistance;

/*
True if force open is required
*/
private Boolean forceOpen;

/*
Limit level
*/
private BigDecimal limitLevel;

/*
Limit distance
*/
private BigDecimal limitDistance;

/*
Lightstreamer price quote identifier
*/
private String quoteId;

/*
Currency
*/
private String currencyCode;

public String getDealReference() { return dealReference; }
public void setDealReference(String dealReference) { this.dealReference=dealReference; }
public String getEpic() { return epic; }
public void setEpic(String epic) { this.epic=epic; }
public String getExpiry() { return expiry; }
public void setExpiry(String expiry) { this.expiry=expiry; }
public Direction getDirection() { return direction; }
public void setDirection(Direction direction) { this.direction=direction; }
public BigDecimal getSize() { return size; }
public void setSize(BigDecimal size) { this.size=size; }
public BigDecimal getLevel() { return level; }
public void setLevel(BigDecimal level) { this.level=level; }
public OrderType getOrderType() { return orderType; }
public void setOrderType(OrderType orderType) { this.orderType=orderType; }
public Boolean getGuaranteedStop() { return guaranteedStop; }
public void setGuaranteedStop(Boolean guaranteedStop) { this.guaranteedStop=guaranteedStop; }
public BigDecimal getStopLevel() { return stopLevel; }
public void setStopLevel(BigDecimal stopLevel) { this.stopLevel=stopLevel; }
public BigDecimal getStopDistance() { return stopDistance; }
public void setStopDistance(BigDecimal stopDistance) { this.stopDistance=stopDistance; }
public Boolean getForceOpen() { return forceOpen; }
public void setForceOpen(Boolean forceOpen) { this.forceOpen=forceOpen; }
public BigDecimal getLimitLevel() { return limitLevel; }
public void setLimitLevel(BigDecimal limitLevel) { this.limitLevel=limitLevel; }
public BigDecimal getLimitDistance() { return limitDistance; }
public void setLimitDistance(BigDecimal limitDistance) { this.limitDistance=limitDistance; }
public String getQuoteId() { return quoteId; }
public void setQuoteId(String quoteId) { this.quoteId=quoteId; }
public String getCurrencyCode() { return currencyCode; }
public void setCurrencyCode(String currencyCode) { this.currencyCode=currencyCode; }
}
