package org.trading.ig.rest.dto.positions.otc.updateOTCPositionV1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
Edit position response
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateOTCPositionV1Response {

/*
Deal reference
*/
private String dealReference;

public String getDealReference() { return dealReference; }
public void setDealReference(String dealReference) { this.dealReference=dealReference; }
}
