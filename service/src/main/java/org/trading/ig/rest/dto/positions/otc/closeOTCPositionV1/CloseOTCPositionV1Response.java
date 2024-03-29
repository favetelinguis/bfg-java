package org.trading.ig.rest.dto.positions.otc.closeOTCPositionV1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
Close position response
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class CloseOTCPositionV1Response {

/*
Deal reference
*/
private String dealReference;

public String getDealReference() { return dealReference; }
public void setDealReference(String dealReference) { this.dealReference=dealReference; }
}
