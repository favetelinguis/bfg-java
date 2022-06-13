package org.trading.ig.rest.dto.workingorders.otc.updateOTCWorkingOrderV2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
Edit working order response
*/
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateOTCWorkingOrderV2Response {

/*
Deal reference of the transaction
*/
private String dealReference;

public String getDealReference() { return dealReference; }
public void setDealReference(String dealReference) { this.dealReference=dealReference; }
}
