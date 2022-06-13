package org.trading.ig.rest.dto.positions.getPositionsV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*

*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetPositionsV2Response {

/*
List of positions
*/
private java.util.List<PositionsItem> positions;

public java.util.List<PositionsItem> getPositions() { return positions; }
public void setPositions(java.util.List<PositionsItem> positions) { this.positions=positions; }
}
